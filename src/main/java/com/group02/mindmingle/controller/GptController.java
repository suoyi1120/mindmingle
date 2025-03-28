package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.gpt.ChatGptRequest;
import com.group02.mindmingle.dto.gpt.ChatGptResponse;
import com.group02.mindmingle.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpt")
@RequiredArgsConstructor
public class GptController {

    private final GptService gptService;

    /**
     * 简单 API - 直接接收文本提示并返回 GPT 生成的响应
     * 
     * @param prompt 用户输入的提示
     * @return GPT 生成的文本响应
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateResponse(@RequestParam String prompt) {
        System.out.println(prompt);
        String response = gptService.generateResponse(prompt);
        return ResponseEntity.ok(response);
    }

    /**
     * 高级 API - 接收完整的 ChatGptRequest 对象并返回完整的 ChatGptResponse
     * 
     * @param request ChatGPT 请求对象
     * @return ChatGPT 响应对象
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatGptResponse> chat(@RequestBody ChatGptRequest request) {
        ChatGptResponse response = gptService.processRequest(request);
        return ResponseEntity.ok(response);
    }
}