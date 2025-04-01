package com.group02.mindmingle.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.group02.mindmingle.dto.gpt.ChatGptRequest;
import com.group02.mindmingle.dto.gpt.ChatGptResponse;
import com.group02.mindmingle.service.GptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    /**
     * 结构化 API - 使用 JSON Schema 约束输出格式
     * 
     * @param prompt     用户输入的提示
     * @param jsonSchema JSON Schema 格式定义
     * @return 结构化的 JSON 响应
     */
    @PostMapping("/structured")
    public ResponseEntity<String> generateStructuredResponse(
            @RequestParam String prompt,
            @RequestBody String jsonSchema) {
        String response = gptService.generateStructuredResponse(
                "gpt-4o-mini", prompt, jsonSchema);
        return ResponseEntity.ok(response);
    }

    /**
     * 高级结构化 API - 接收完整的 ChatGptRequest 对象和 JSON Schema
     * 
     * @param request    ChatGPT 请求对象
     * @param jsonSchema JSON Schema 格式定义
     * @return 结构化的 ChatGPT 响应对象
     */
    @PostMapping("/structured-chat")
    public ResponseEntity<ChatGptResponse> structuredChat(
            @RequestBody ChatGptRequest request,
            @RequestParam String jsonSchema) {
        ChatGptResponse response = gptService.processStructuredRequest(request, jsonSchema);
        return ResponseEntity.ok(response);
    }

    /**
     * 支持新版 OpenAI API 格式的结构化输出接口
     * 
     * @param requestBody 包含 model、input 和 text 字段的请求体
     * @return 结构化的 JSON 响应
     */
    @PostMapping("/openai-format")
    public ResponseEntity<String> openAiFormatRequest(@RequestBody Map<String, Object> requestBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // 提取模型名称
            String model = (String) requestBody.get("model");

            // 提取消息列表
            List<Map<String, String>> inputMessages = (List<Map<String, String>>) requestBody.get("input");
            List<ChatGptRequest.Message> messages = new ArrayList<>();

            for (Map<String, String> inputMessage : inputMessages) {
                String role = inputMessage.get("role");
                String content = inputMessage.get("content");
                messages.add(new ChatGptRequest.Message(role, content));
            }

            // 提取 JSON Schema
            Map<String, Object> textFormat = (Map<String, Object>) ((Map<String, Object>) requestBody.get("text"))
                    .get("format");
            Map<String, Object> schema = (Map<String, Object>) textFormat.get("schema");

            // 将 schema 转换回 JSON 字符串
            String jsonSchema = mapper.writeValueAsString(schema);

            // 构建请求并调用服务
            ChatGptRequest request = new ChatGptRequest(model, messages, 0.7, 3000);
            String response = gptService.generateStructuredResponse(model, messages, jsonSchema);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("处理请求时发生错误: " + e.getMessage());
        }
    }
}