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
                .addMappings(mapper -> mapper.map(
                        src -> src.getCreatedAt() != null ? src.getCreatedAt().format(DATE_FORMATTER) : null,
                        GameDto::setCreatedAt));
    }

    public List<GameDto> getAllGames() {
        return gameRepository.findAll().stream()
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

        try {
            // 创建Gemini请求
            Parts parts = new Parts();
            parts.setText(promptText);

            // 调用Gemini API生成HTML代码
            String geminiResponse = geminiService.generateTextResponse(parts);
            logger.info("Gemini API返回响应: {}", geminiResponse);

            // 从响应中提取HTML代码
            String htmlCode = extractHtmlCode(geminiResponse);
            if (htmlCode == null || htmlCode.trim().isEmpty()) {
                throw new IllegalStateException("无法从Gemini API响应中提取HTML代码");
            }

            // 创建HTML文件
            String fileName = "game_" + gameId + "_" + System.currentTimeMillis() + ".html";
            MultipartFile htmlFile = createHtmlFile(htmlCode, fileName);

            // 上传文件到Azure存储
            String fileUrl = fileUploadService.uploadFile(htmlFile, "my_test_files");

            // 更新游戏的存储URL
            game.setStorageUrl(fileUrl);
            Game updatedGame = gameRepository.save(game);

            // 返回更新后的游戏DTO
            return modelMapper.map(updatedGame, GameDto.class);
        } catch (Exception e) {
            logger.error("生成游戏HTML时发生错误", e);
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
