package com.group02.mindmingle.controller;

import com.group02.mindmingle.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    /**
     * 发送聊天消息到AI助手
     * 这个端点受到Spring Security保护，需要有效的JWT才能访问
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, String> messageBody,
                                         Authentication authentication) {
        // 从Authentication对象中获取当前用户信息
        User currentUser = (User) authentication.getPrincipal();

        // 获取用户消息
        String message = messageBody.get("message");

        // 记录用户ID和消息 (实际项目中可能会存储到数据库)
        System.out.println("用户ID: " + currentUser.getId() + " 发送消息: " + message);

        // 根据用户消息生成AI回复（示例中使用简单回复）
        String botResponse = generateBotResponse(message);

        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", currentUser.getId());
        response.put("username", currentUser.getUsername());
        response.put("message", message);
        response.put("botResponse", botResponse);

        return ResponseEntity.ok(response);
    }

    /**
     * 测试端点：验证JWT认证有效性
     * 返回当前认证用户的信息
     */
    @GetMapping("/test")
    public ResponseEntity<?> testAuth(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "JWT认证成功");
        response.put("user", Map.of(
                "id", currentUser.getId(),
                "username", currentUser.getUsername(),
                "email", currentUser.getEmail(),
                "roles", currentUser.getAuthorities()));

        return ResponseEntity.ok(response);
    }

    /**
     * 生成AI助手回复（简单实现，实际项目可能会调用AI服务）
     */
    private String generateBotResponse(String message) {
        message = message.toLowerCase();

        if (message.contains("你好") || message.contains("嗨") || message.contains("hi")) {
            return "你好！有什么我可以帮助你的吗？";
        } else if (message.contains("焦虑") || message.contains("压力") || message.contains("紧张")) {
            return "感到焦虑或压力是很常见的。尝试深呼吸，每天冥想5分钟，或者和朋友聊聊你的感受，这些都有助于缓解压力。";
        } else if (message.contains("睡眠") || message.contains("失眠")) {
            return "良好的睡眠对心理健康非常重要。建议固定作息时间，睡前一小时避免使用电子设备，创造舒适的睡眠环境。";
        } else if (message.contains("感谢") || message.contains("谢谢")) {
            return "不客气！随时可以来聊天。";
        } else {
            return "谢谢你的分享。心理健康是一个持续的旅程，如果你需要更多支持，随时可以找专业人士咨询。";
        }
    }
}
