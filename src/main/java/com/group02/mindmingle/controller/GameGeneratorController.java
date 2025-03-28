package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.game.ApiResponse;
import com.group02.mindmingle.dto.game.GameGeneratorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/admin/game-generator")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class GameGeneratorController {

    // 存储生成的游戏
    private static final Map<String, GameGeneratorResponse> games = new ConcurrentHashMap<>();

    /**
     * 生成新游戏
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<GameGeneratorResponse>> generateGame(@RequestBody Map<String, String> request) {
        System.out.println("generateGame请求已接收: " + request);
        try {
            String prompt = request.getOrDefault("prompt", "");
            if (prompt.isEmpty()) {
                return ResponseEntity.badRequest().body(
                        new ApiResponse<>(false, null, "游戏描述不能为空"));
            }

            // 生成游戏ID
            String gameId = UUID.randomUUID().toString();
            System.out.println("生成游戏ID: " + gameId);

            // 从提示中提取游戏名称
            String gameName = "卡片游戏 " + gameId.substring(0, 8);

            // 创建游戏响应对象
            GameGeneratorResponse response = new GameGeneratorResponse();
            response.setGameId(gameId);
            response.setGameName(gameName);
            response.setPrompt(prompt);
            response.setStatus("processing");
            response.setMessage("游戏正在生成中...");

            // 存储游戏
            games.put(gameId, response);
            System.out.println("游戏数据已初始化并存储: " + response.getGameId());

            // 模拟异步处理，实际项目中这里应该使用线程池或消息队列
            new Thread(() -> {
                try {
                    System.out.println("开始异步生成游戏: " + gameId);
                    // 减少模拟处理时间，加快响应
                    Thread.sleep(1000); // 从5000ms改为1000ms

                    // 更新游戏状态
                    String previewUrl = "/api/admin/game-generator/game/" + gameId + "/play";
                    response.setStatus("success");
                    response.setMessage("游戏生成成功");
                    response.setPreviewUrl(previewUrl);

                    // 更新存储
                    games.put(gameId, response);
                    System.out.println("游戏生成完成，状态已更新: " + gameId + ", URL: " + previewUrl);
                } catch (Exception e) {
                    System.err.println("游戏生成异常: " + e.getMessage());
                    response.setStatus("error");
                    response.setMessage("游戏生成失败: " + e.getMessage());
                    games.put(gameId, response);
                }
            }).start();

            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ApiResponse<>(true, response, "游戏生成请求已提交"));
        } catch (Exception e) {
            System.err.println("处理游戏生成请求异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse<>(false, null, "服务器错误: " + e.getMessage()));
        }
    }

    /**
     * 获取游戏详情
     */
    @GetMapping("/game/{gameId}")
    public ResponseEntity<ApiResponse<GameGeneratorResponse>> getGameDetails(@PathVariable String gameId) {
        System.out.println("getGameDetails请求: " + gameId);
        GameGeneratorResponse game = games.get(gameId);

        if (game == null) {
            System.out.println("游戏不存在: " + gameId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ApiResponse<>(false, null, "游戏不存在"));
        }

        System.out.println("获取游戏成功: " + gameId + ", 状态: " + game.getStatus());
        return ResponseEntity.ok(new ApiResponse<>(true, game, "获取游戏成功"));
    }

    /**
     * 获取所有游戏列表
     */
    @GetMapping("/games")
    public ResponseEntity<ApiResponse<List<GameGeneratorResponse>>> getGamesList() {
        List<GameGeneratorResponse> gamesList = new ArrayList<>(games.values());
        System.out.println("获取游戏列表: " + gamesList.size() + "个游戏");
        return ResponseEntity.ok(new ApiResponse<>(true, gamesList, "获取游戏列表成功"));
    }

    /**
     * 返回游戏HTML页面
     */
    @GetMapping(value = "/game/{gameId}/play", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> playGame(
            @PathVariable String gameId,
            @RequestParam(required = false, defaultValue = "点击查看卡片内容") String cardText,
            @RequestParam(required = false, defaultValue = "#4A90E2") String cardColor) {

        System.out.println("playGame请求: " + gameId);
        GameGeneratorResponse game = games.get(gameId);
        if (game == null) {
            System.out.println("游戏不存在: " + gameId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    "<html><body><h1>游戏不存在</h1></body></html>");
        }

        System.out.println("生成游戏HTML页面: " + gameId);
        String gameHtml = generateCardHtml(gameId, cardText, cardColor, game.getGameName());
        System.out.println("游戏HTML页面生成完成: " + gameId + " (HTML长度: " + gameHtml.length() + "字节)");

        return ResponseEntity.ok()
                .header("Content-Type", "text/html;charset=UTF-8")
                .header("Content-Security-Policy", "frame-ancestors 'self' http://localhost:3000")
                .body(gameHtml);
    }

    /**
     * 生成卡片HTML
     */
    private String generateCardHtml(String gameId, String cardText, String cardColor, String gameName) {
        System.out.println("调用generateCardHtml: " + gameId);
        try {
            // 读取静态HTML模板文件
            ClassPathResource resource = new ClassPathResource("static/games/card-game-template.html");
            InputStream inputStream = resource.getInputStream();
            String html = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            // 替换模板中的占位符
            html = html.replace("{{gameId}}", gameId)
                    .replace("{{cardText}}", cardText)
                    .replace("{{cardColor}}", cardColor)
                    .replace("{{gameName}}", gameName);

            return html;
        } catch (IOException e) {
            System.err.println("读取HTML模板文件失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("无法读取游戏HTML模板: " + e.getMessage());
        }
    }

    /**
     * 简化的测试端点，直接返回游戏HTML（无需游戏生成过程）
     */
    @GetMapping(value = "/test-game", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> testGamePage(
            @RequestParam(required = false, defaultValue = "测试游戏内容") String cardText,
            @RequestParam(required = false, defaultValue = "#5A3FFF") String cardColor) {

        System.out.println("testGamePage请求接收: cardText=" + cardText);

        // 生成测试游戏ID
        String testGameId = "test-" + System.currentTimeMillis();
        String testGameName = "测试卡片游戏";

        try {
            // 读取测试游戏HTML模板文件
            ClassPathResource resource = new ClassPathResource("static/games/test-game-template.html");
            InputStream inputStream = resource.getInputStream();
            String html = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

            // 替换模板中的占位符
            html = html.replace("{{gameId}}", testGameId)
                    .replace("{{cardText}}", cardText)
                    .replace("{{cardColor}}", cardColor)
                    .replace("{{gameName}}", testGameName);

            System.out.println("测试游戏HTML生成完成: (HTML长度: " + html.length() + "字节)");

            return ResponseEntity.ok()
                    .header("Content-Type", "text/html;charset=UTF-8")
                    .header("Content-Security-Policy", "frame-ancestors 'self' http://localhost:3000")
                    .body(html);
        } catch (IOException e) {
            System.err.println("读取测试游戏HTML模板文件失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("无法读取测试游戏HTML模板: " + e.getMessage());
        }
    }
}
