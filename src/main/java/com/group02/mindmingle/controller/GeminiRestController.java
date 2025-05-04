package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.gemini.ChatRequest;
import com.group02.mindmingle.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/gemini")
public class GeminiRestController {

    private static final Logger logger = LoggerFactory.getLogger(GeminiRestController.class);

    @Autowired
    private GeminiService geminiService;

    /**
     * 聊天接口 - 发送聊天请求并获取响应
     * 
     * @param chatRequest 聊天请求
     * @return 聊天响应文本
     */
    @PostMapping("/chat")
    public ResponseEntity<String> chatWithGemini(@RequestBody ChatRequest chatRequest) {
        logger.info("接收到聊天请求：{}", chatRequest);
        String response = geminiService.chat(chatRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 游戏HTML生成接口 - 根据用户描述生成游戏HTML
     * 
     * @param userInput 用户输入
     * @return 生成的HTML代码
     */
    @PostMapping("/generate-game")
    public ResponseEntity<String> generateGameHTML(@RequestBody String userInput) {
        try {
            logger.info("接收到游戏生成请求，用户输入：{}", userInput);
            String htmlResponse = geminiService.generateGameHtml(userInput);
            return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
        } catch (IOException e) {
            logger.error("生成游戏HTML时发生错误：", e);
            return new ResponseEntity<>("生成游戏HTML时发生错误：" + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}