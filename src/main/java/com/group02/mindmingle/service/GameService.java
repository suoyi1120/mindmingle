package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.game.GameDto;
import com.group02.mindmingle.dto.game.CreateGameRequest;
import com.group02.mindmingle.dto.gemini.Parts;
import com.group02.mindmingle.exception.ResourceNotFoundException;
import com.group02.mindmingle.model.Game;
import com.group02.mindmingle.repository.GameRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;
    private final GeminiService geminiService;
    private final FileUploadService fileUploadService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*([\\s\\S]*?)\\s*```");

    @Autowired
    public GameService(
            GameRepository gameRepository,
            ModelMapper modelMapper,
            GeminiService geminiService,
            FileUploadService fileUploadService) {
        this.gameRepository = gameRepository;
        this.modelMapper = modelMapper;
        this.geminiService = geminiService;
        this.fileUploadService = fileUploadService;

        // 配置ModelMapper以正确处理日期格式化
        modelMapper.createTypeMap(Game.class, GameDto.class)
                .addMappings(mapper -> {
                    mapper.map(
                            src -> src.getCreatedAt() != null ? src.getCreatedAt().format(DATE_FORMATTER) : null,
                            GameDto::setCreatedAt);
                    mapper.map(
                            src -> src.getUpdatedAt() != null ? src.getUpdatedAt().format(DATE_FORMATTER) : null,
                            GameDto::setUpdatedAt);
                });
    }

    public List<GameDto> getAllGames() {
        return gameRepository.findAll().stream()
                .sorted((g1, g2) -> {
                    // 首先按更新日期排序（从新到旧）
                    LocalDateTime date1 = g1.getUpdatedAt() != null ? g1.getUpdatedAt() : g1.getCreatedAt();
                    LocalDateTime date2 = g2.getUpdatedAt() != null ? g2.getUpdatedAt() : g2.getCreatedAt();
                    // 降序排序（从新到旧）
                    return date2.compareTo(date1);
                })
                .map(game -> modelMapper.map(game, GameDto.class))
                .collect(Collectors.toList());
    }

    /**
     * 通过ID获取游戏
     * 
     * @param id 游戏ID
     * @return 游戏DTO
     * @throws ResourceNotFoundException 如果游戏不存在
     */
    public GameDto getGameById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + id + "的游戏"));
        return modelMapper.map(game, GameDto.class);
    }

    public GameDto createGame(CreateGameRequest request) {
        Game game = Game.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl() != null && !request.getImageUrl().isEmpty()
                        ? request.getImageUrl()
                        : "https://via.placeholder.com/300x150")
                .build();

        Game savedGame = gameRepository.save(game);
        return modelMapper.map(savedGame, GameDto.class);
    }

    /**
     * 更新游戏信息
     * 
     * @param id      游戏ID
     * @param request 更新请求
     * @return 更新后的游戏DTO
     * @throws ResourceNotFoundException 如果游戏不存在
     */
    public GameDto updateGame(Long id, CreateGameRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + id + "的游戏"));

        // 保存旧的封面URL，用于后续删除
        String oldImageUrl = game.getImageUrl();

        // 更新游戏信息
        game.setTitle(request.getTitle());
        game.setDescription(request.getDescription());
        game.setCategory(request.getCategory());

        // 只有在提供新图片URL时才更新
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()
                && !request.getImageUrl().equals(oldImageUrl)) {
            game.setImageUrl(request.getImageUrl());

            // 保存更新后的游戏信息
            Game updatedGame = gameRepository.save(game);

            // 如果旧图片URL不是默认图片并且不是新提供的图片URL，则删除旧图片
            if (oldImageUrl != null && !oldImageUrl.isEmpty()
                    && !oldImageUrl.contains("placeholder.com")
                    && !oldImageUrl.equals(request.getImageUrl())) {
                try {
                    logger.info("删除旧的游戏封面图片: {}", oldImageUrl);
                    fileUploadService.deleteFile(oldImageUrl);
                } catch (Exception e) {
                    // 记录错误但不影响主流程
                    logger.error("删除旧的游戏封面图片时出错: {}", e.getMessage(), e);
                }
            }

            return modelMapper.map(updatedGame, GameDto.class);
        } else {
            // 如果没有更新封面，直接保存其他信息
            Game updatedGame = gameRepository.save(game);
            return modelMapper.map(updatedGame, GameDto.class);
        }
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    public boolean gameExists(Long id) {
        return gameRepository.existsById(id);
    }

    /**
     * 根据用户指示生成游戏HTML并更新游戏的存储URL
     * 
     * @param gameId     游戏ID
     * @param promptText 用户提示文本
     * @return 更新后的游戏DTO
     * @throws ResourceNotFoundException 如果游戏不存在
     * @throws IOException               如果文件操作出错
     */
    public GameDto generateGameHtml(Long gameId, String promptText) throws ResourceNotFoundException, IOException {
        // 获取游戏
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为" + gameId + "的游戏"));

        logger.info("为游戏 '{}' (ID: {}) 生成HTML内容", game.getTitle(), gameId);

        try {
            // 使用新的方法生成HTML代码
            String geminiResponse = geminiService.generateGameHtml(promptText);
            logger.debug("Gemini API返回响应长度: {} 字符", geminiResponse.length());

            // 从响应中提取HTML代码
            String htmlCode = extractHtmlCode(geminiResponse);
            if (htmlCode == null || htmlCode.trim().isEmpty()) {
                logger.error("无法从Gemini API响应中提取HTML代码");
                throw new IllegalStateException("无法从Gemini API响应中提取HTML代码");
            }

            logger.info("成功提取HTML代码，长度: {} 字符", htmlCode.length());

            // 创建HTML文件
            String fileName = "game_" + gameId + "_" + System.currentTimeMillis() + ".html";
            MultipartFile htmlFile = createHtmlFile(htmlCode, fileName);
            logger.info("创建了HTML文件: {}, 大小: {} 字节", fileName, htmlFile.getSize());

            // 上传文件到Azure存储
            String fileUrl = fileUploadService.uploadFile(htmlFile, "my_test_files");
            if (fileUrl == null || fileUrl.isEmpty()) {
                logger.error("文件上传失败，未返回有效URL");
                throw new IOException("文件上传失败，未返回有效URL");
            }

            logger.info("文件上传成功，URL: {}", fileUrl);

            // 更新游戏的存储URL
            game.setStorageUrl(fileUrl);
            Game updatedGame = gameRepository.save(game);
            logger.info("游戏记录已更新，存储URL: {}", updatedGame.getStorageUrl());

            // 验证更新是否成功
            Game verifiedGame = gameRepository.findById(gameId).orElse(null);
            if (verifiedGame == null || verifiedGame.getStorageUrl() == null
                    || verifiedGame.getStorageUrl().isEmpty()) {
                logger.error("游戏URL保存验证失败");
                throw new IllegalStateException("游戏URL保存验证失败");
            }

            logger.info("验证成功，游戏URL已保存: {}", verifiedGame.getStorageUrl());

            // 返回更新后的游戏DTO
            return modelMapper.map(updatedGame, GameDto.class);
        } catch (Exception e) {
            logger.error("生成游戏HTML时发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 从Gemini API响应中提取HTML代码
     * 
     * @param response Gemini API响应
     * @return 提取的HTML代码，如果未找到则返回null
     */
    private String extractHtmlCode(String response) {
        Matcher matcher = HTML_CODE_PATTERN.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 创建包含HTML代码的MultipartFile
     * 
     * @param htmlCode HTML代码
     * @param fileName 文件名
     * @return 创建的MultipartFile
     */
    private MultipartFile createHtmlFile(String htmlCode, String fileName) {
        byte[] content = htmlCode.getBytes(StandardCharsets.UTF_8);
        return new CustomMultipartFile(content, fileName);
    }

    /**
     * 自定义MultipartFile实现类
     */
    private static class CustomMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public CustomMultipartFile(byte[] content, String name) {
            this.content = content;
            this.name = name;
            this.originalFilename = name;
            this.contentType = "text/html";
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOriginalFilename() {
            return originalFilename;
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public boolean isEmpty() {
            return content == null || content.length == 0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            throw new UnsupportedOperationException("不支持transferTo操作");
        }
    }
}
