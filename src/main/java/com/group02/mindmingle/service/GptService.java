package com.group02.mindmingle.service;

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

import java.util.List;
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
                    .temperature(0.7)
                    .maxTokens(800)
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
}