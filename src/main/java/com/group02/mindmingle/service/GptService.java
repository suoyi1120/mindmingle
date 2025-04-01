package com.group02.mindmingle.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group02.mindmingle.dto.gpt.ChatGptRequest;
import com.group02.mindmingle.dto.gpt.ChatGptResponse;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GptService {

    private final OpenAiService openAiService;

    @Value("${openai.api.model}")
    private String defaultModel;

    /**
     * 使用默认模型发送请求到 OpenAI API
     * 
     * @param prompt 用户输入的提示
     * @return 处理后的 ChatGPT 响应
     */
    public String generateResponse(String prompt) {
        return generateResponse(defaultModel, prompt);
    }

    /**
     * 使用指定模型发送请求到 OpenAI API
     * 
     * @param model  使用的 GPT 模型
     * @param prompt 用户输入的提示
     * @return 处理后的 ChatGPT 响应
     */
    public String generateResponse(String model, String prompt) {
        try {
            log.info("发送请求到 GPT API，模型: {}, 提示: {}", model, prompt);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(new ChatMessage("user", prompt)))
                    // .temperature(0.7)
                    // .maxTokens(5000)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(chatCompletionRequest);

            if (result.getChoices() == null || result.getChoices().isEmpty()) {
                throw new RuntimeException("GPT API 返回了空响应");
            }

            String response = result.getChoices().get(0).getMessage().getContent();
            log.info("收到 GPT 响应: {}", response);

            return response;
        } catch (Exception e) {
            log.error("调用 GPT API 时发生错误", e);
            throw new RuntimeException("处理 GPT 请求时发生错误: " + e.getMessage(), e);
        }
    }

    /**
     * 使用JSON Schema规定输出结构的请求到 OpenAI API
     * 注意：需要使用支持responseFormat的OpenAI API版本
     * 
     * @param model      使用的 GPT 模型
     * @param prompt     用户输入的提示
     * @param jsonSchema 规定输出结构的JSON Schema
     * @return 按照指定结构返回的 ChatGPT 响应
     */
    public String generateStructuredResponse(String model, String prompt, String jsonSchema) {
        try {
            log.info("发送结构化请求到 GPT API，模型: {}, 提示: {}", model, prompt);

            // 构建包含JSON Schema指令的提示
            String structuredPrompt = String.format(
                    "我需要你以特定的JSON格式回复。请严格按照以下JSON Schema格式输出：\n\n%s\n\n" +
                            "请确保你的回复是有效的JSON并且符合上述schema。现在，请回答以下问题：\n\n%s",
                    jsonSchema, prompt);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(List.of(new ChatMessage("user", structuredPrompt)))
                    .temperature(0.7)
                    .maxTokens(3000)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(chatCompletionRequest);

            if (result.getChoices() == null || result.getChoices().isEmpty()) {
                throw new RuntimeException("GPT API 返回了空响应");
            }

            String response = result.getChoices().get(0).getMessage().getContent();
            log.info("收到结构化 GPT 响应: {}", response.substring(0, Math.min(response.length(), 100)) + "...");

            return response;
        } catch (Exception e) {
            log.error("调用结构化 GPT API 时发生错误", e);
            throw new RuntimeException("处理结构化 GPT 请求时发生错误: " + e.getMessage(), e);
        }
    }

    /**
     * 使用JSON Schema规定输出结构的高级请求到 OpenAI API
     * 
     * @param model      使用的 GPT 模型
     * @param messages   消息列表
     * @param jsonSchema 规定输出结构的JSON Schema
     * @return 按照指定结构返回的 ChatGPT 响应
     */
    public String generateStructuredResponse(String model, List<ChatGptRequest.Message> messages, String jsonSchema) {
        try {
            log.info("发送高级结构化请求到 GPT API，模型: {}", model);

            // 转换消息格式
            List<ChatMessage> chatMessages = messages.stream()
                    .map(msg -> new ChatMessage(msg.getRole(), msg.getContent()))
                    .collect(Collectors.toList());

            // 添加系统消息指导输出格式
            ChatMessage systemMessage = new ChatMessage("system",
                    "请以JSON格式回复，严格遵循以下JSON Schema：\n\n" + jsonSchema);

            // 将系统消息添加到消息列表的开头
            chatMessages.add(0, systemMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(model)
                    .messages(chatMessages)
                    .temperature(0.7)
                    .maxTokens(3000)
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(chatCompletionRequest);

            if (result.getChoices() == null || result.getChoices().isEmpty()) {
                throw new RuntimeException("GPT API 返回了空响应");
            }

            String response = result.getChoices().get(0).getMessage().getContent();
            log.info("收到高级结构化 GPT 响应");

            return response;
        } catch (Exception e) {
            log.error("调用高级结构化 GPT API 时发生错误", e);
            throw new RuntimeException("处理高级结构化 GPT 请求时发生错误: " + e.getMessage(), e);
        }
    }

    /**
     * 高级 API - 直接使用 ChatGptRequest 请求对象
     * 
     * @param request ChatGPT 请求对象
     * @return ChatGPT 响应对象
     */
    public ChatGptResponse processRequest(ChatGptRequest request) {
        try {
            log.info("处理高级 GPT 请求: {}", request);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model(request.getModel())
                    .messages(request.getMessages().stream()
                            .map(msg -> new ChatMessage(msg.getRole(), msg.getContent()))
                            .collect(Collectors.toList()))
                    .temperature(request.getTemperature())
                    .maxTokens(request.getMax_tokens())
                    .build();

            ChatCompletionResult result = openAiService.createChatCompletion(chatCompletionRequest);

            // 将 ChatCompletionResult 转换为我们的 ChatGptResponse
            // 这里为简化只转换了部分字段
            ChatGptResponse response = new ChatGptResponse();
            response.setId(result.getId());
            response.setObject("chat.completion");
            response.setCreated(result.getCreated());
            response.setModel(result.getModel());

            List<ChatGptResponse.Choice> choices = result.getChoices().stream()
                    .map(choice -> {
                        ChatGptRequest.Message message = new ChatGptRequest.Message(
                                choice.getMessage().getRole(),
                                choice.getMessage().getContent());
                        return new ChatGptResponse.Choice(message, choice.getFinishReason(), choice.getIndex());
                    })
                    .collect(Collectors.toList());

            response.setChoices(choices);

            // 使用计算的 token 数(实际环境中可能需要更准确地计算)
            ChatGptResponse.Usage usage = new ChatGptResponse.Usage();
            if (result.getUsage() != null) {
                usage.setPrompt_tokens(result.getUsage().getPromptTokens());
                usage.setCompletion_tokens(result.getUsage().getCompletionTokens());
                usage.setTotal_tokens(result.getUsage().getTotalTokens());
            }
            response.setUsage(usage);

            log.info("GPT 请求处理完成");
            return response;
        } catch (Exception e) {
            log.error("处理高级 GPT 请求时发生错误", e);
            throw new RuntimeException("处理 GPT 请求时发生错误: " + e.getMessage(), e);
        }
    }

    /**
     * 高级 API - 使用 ChatGptRequest 请求对象和 JSON Schema 结构化输出
     * 
     * @param request    ChatGPT 请求对象
     * @param jsonSchema 规定输出结构的JSON Schema
     * @return 结构化的 ChatGPT 响应对象
     */
    public ChatGptResponse processStructuredRequest(ChatGptRequest request, String jsonSchema) {
        try {
            log.info("处理结构化高级 GPT 请求: {}", request);

            // 创建一个新的消息列表，添加系统消息指导输出格式
            List<ChatGptRequest.Message> messages = new ArrayList<>(request.getMessages());
            messages.add(0, new ChatGptRequest.Message("system",
                    "请以JSON格式回复，严格遵循以下JSON Schema：\n\n" + jsonSchema));

            // 更新请求中的消息
            ChatGptRequest structuredRequest = new ChatGptRequest(
                    request.getModel(),
                    messages,
                    request.getTemperature(),
                    request.getMax_tokens());

            // 处理请求
            return processRequest(structuredRequest);
        } catch (Exception e) {
            log.error("处理结构化高级 GPT 请求时发生错误", e);
            throw new RuntimeException("处理结构化 GPT 请求时发生错误: " + e.getMessage(), e);
        }
    }
}