package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.common.ApiResponse;
import com.group02.mindmingle.dto.game.GameDto;
import com.group02.mindmingle.dto.game.GameGeneratorResponse;
import com.group02.mindmingle.exception.ResourceNotFoundException;
import com.group02.mindmingle.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/game-generator")
public class GameGeneratorController {

    private static final Logger logger = LoggerFactory.getLogger(GameGeneratorController.class);

    @Autowired
    private GameService gameService;

    /**
     * 根据用户指示生成游戏HTML
     * 
     * @param request 包含文本提示和游戏ID的请求
     * @return 生成结果响应
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<GameGeneratorResponse>> generateGame(@RequestBody Map<String, Object> request) {
        try {
            // 从请求中提取text和id
            String text = (String) request.get("text");
            Long gameId = null;

            // 尝试将id转换为Long类型
            Object idObj = request.get("id");
            if (idObj instanceof Integer) {
                gameId = ((Integer) idObj).longValue();
            } else if (idObj instanceof Long) {
                gameId = (Long) idObj;
            } else if (idObj instanceof String) {
                try {
                    gameId = Long.parseLong((String) idObj);
                } catch (NumberFormatException e) {
                    logger.error("无法将ID解析为数字: {}", idObj);
                }
            }

            // 验证必需参数
            if (text == null || text.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.<GameGeneratorResponse>builder()
                                .success(false)
                                .message("缺少必需的'text'参数")
                                .build());
            }

            if (gameId == null) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.<GameGeneratorResponse>builder()
                                .success(false)
                                .message("缺少必需的'id'参数或格式不正确")
                                .build());
            }

            logger.info("开始为游戏ID {} 生成HTML，提示词: {}", gameId, text);

            // 调用服务生成游戏HTML
            GameDto updatedGame = gameService.generateGameHtml(gameId, text);

            // 记录更新后的游戏信息
            logger.info("游戏HTML生成成功: ID={}, 标题={}, 存储URL={}",
                    updatedGame.getId(), updatedGame.getTitle(), updatedGame.getStorageUrl());

            if (updatedGame.getStorageUrl() == null || updatedGame.getStorageUrl().isEmpty()) {
                logger.error("生成的游戏没有有效的存储URL");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        ApiResponse.<GameGeneratorResponse>builder()
                                .success(false)
                                .message("游戏生成成功但没有存储URL")
                                .build());
            }

            // 构建响应
            GameGeneratorResponse response = GameGeneratorResponse.builder()
                    .gameId(updatedGame.getId())
                    .title(updatedGame.getTitle())
                    .storageUrl(updatedGame.getStorageUrl())
                    .build();

            // 日志记录响应对象
            logger.info("构建的响应对象: gameId={}, title={}, storageUrl={}",
                    response.getGameId(), response.getTitle(), response.getStorageUrl());

            // 返回成功响应
            ApiResponse<GameGeneratorResponse> apiResponse = ApiResponse.<GameGeneratorResponse>builder()
                    .success(true)
                    .message("游戏HTML生成成功")
                    .data(response)
                    .build();

            // 确保响应数据存在
            if (apiResponse.getData() == null) {
                logger.error("响应数据为null");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                        ApiResponse.<GameGeneratorResponse>builder()
                                .success(false)
                                .message("内部服务器错误：响应数据为null")
                                .build());
            }

            // 日志记录最终API响应
            logger.info("API响应: success={}, message={}, data={}",
                    apiResponse.isSuccess(), apiResponse.getMessage(),
                    apiResponse.getData() != null ? "存在" : "null");

            return ResponseEntity.ok(apiResponse);

        } catch (ResourceNotFoundException e) {
            logger.error("生成游戏HTML时找不到资源", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ApiResponse.<GameGeneratorResponse>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        } catch (IOException e) {
            logger.error("生成游戏HTML时发生IO错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<GameGeneratorResponse>builder()
                            .success(false)
                            .message("文件处理错误: " + e.getMessage())
                            .build());
        } catch (Exception e) {
            logger.error("生成游戏HTML时发生错误", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<GameGeneratorResponse>builder()
                            .success(false)
                            .message("服务器错误: " + e.getMessage())
                            .build());
        }
    }
}
