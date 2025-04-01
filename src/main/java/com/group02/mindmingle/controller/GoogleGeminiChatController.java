package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.gemini.ChatRequest; // <--- 1. 导入 DTO
import com.group02.mindmingle.service.SimpleChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/google-gemini")
public class GoogleGeminiChatController {

    private final SimpleChatService simpleChatService;

    public GoogleGeminiChatController(SimpleChatService simpleChatService) {
        this.simpleChatService = simpleChatService;
    }

    @PostMapping("/chat")
    // 2. 修改返回类型为 String
    // 3. 修改方法参数为 @RequestBody ChatRequest request
    public String chat(@RequestBody ChatRequest request) {
        // 4. 从 request 对象中获取 prompt 字段的值
        String userPrompt = request.getPrompt();

        // 5. 调用 service 并直接返回 String 结果
        return this.simpleChatService.chat(userPrompt);
    }
}