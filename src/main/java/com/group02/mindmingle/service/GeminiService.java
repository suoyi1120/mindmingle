package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.gemini.ChatRequest;
import com.group02.mindmingle.dto.gemini.GeminiResponse;
import com.group02.mindmingle.dto.gemini.Prompt;
import com.group02.mindmingle.dto.gemini.Contents;
import com.group02.mindmingle.dto.gemini.Parts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ResourceLoader resourceLoader;

    public GeminiService(RestTemplate restTemplate, ResourceLoader resourceLoader) {
        this.restTemplate = restTemplate;
        this.resourceLoader = resourceLoader;
    }

    /**
     * 调用Gemini API发送请求并获取响应
     * 
     * @param prompt API请求对象
     * @return API响应对象
     */
    public GeminiResponse callGeminiAPI(Prompt prompt) {
        HttpEntity<Prompt> requestEntity = new HttpEntity<>(prompt);
        logger.info("发送请求到Gemini API，完整prompt结构：\n{}", prompt.toString());

        ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                GeminiResponse.class);
        return response.getBody();
    }

    /**
     * 从Prompt获取响应文本
     * 
     * @param prompt API请求对象
     * @return 响应文本
     */
    public String getResponseText(Prompt prompt) {
        GeminiResponse response = callGeminiAPI(prompt);
        if (response == null || response.getCandidates() == null || response.getCandidates().isEmpty()) {
            return "无响应数据";
        }

        GeminiResponse.Candidate candidate = response.getCandidates().get(0);
        if (candidate.getContent() == null || candidate.getContent().getParts() == null ||
                candidate.getContent().getParts().isEmpty()) {
            return "无内容数据";
        }

        StringBuilder result = new StringBuilder();
        for (GeminiResponse.Part part : candidate.getContent().getParts()) {
            if (part.getText() != null) {
                result.append(part.getText());
            }
        }

        return result.toString();
    }

    /**
     * 聊天功能，将系统提示和用户提示整合到一次请求中
     * 
     * @param chatRequest 包含系统提示和用户输入的请求
     * @return 响应文本
     */
    public String chat(ChatRequest chatRequest) {
        Prompt prompt = new Prompt();
        List<Contents> contentsList = new ArrayList<>();

        // 创建一个Contents对象
        Contents contents = new Contents();
        contents.setRole("user");

        // 创建一个Parts对象，包含系统提示和用户输入
        Parts parts = new Parts();

        // 如果有系统提示，则将系统提示和用户输入组合在一起
        if (chatRequest.getSystemPrompt() != null && !chatRequest.getSystemPrompt().isEmpty()) {
            String combinedText = chatRequest.getSystemPrompt() + "\n\n" + chatRequest.getPrompt();
            parts.setText(combinedText);
        } else {
            parts.setText(chatRequest.getPrompt());
        }

        // 设置Parts列表到Contents中
        List<Parts> partsList = new ArrayList<>();
        partsList.add(parts);
        contents.setParts(partsList);

        // 添加Contents到Prompt中
        contentsList.add(contents);
        prompt.setContents(contentsList);

        return getResponseText(prompt);
    }

    /**
     * 读取指定路径的提示文件内容
     * 
     * @param path 提示文件路径
     * @return 文件内容字符串
     * @throws IOException 如果读取文件时发生错误
     */
    public String readPromptFile(String path) throws IOException {
        logger.info("正在读取提示文件: {}", path);
        Resource resource = resourceLoader.getResource("classpath:" + path);

        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String content = FileCopyUtils.copyToString(reader);
            logger.info("成功读取提示文件，内容长度: {} 字符", content.length());
            return content;
        } catch (IOException e) {
            logger.error("读取提示文件时发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 使用游戏生成提示模板和用户输入构建游戏生成请求
     * 
     * @param userInput 用户输入的游戏描述
     * @return 生成的HTML代码
     * @throws IOException 如果读取提示模板文件时发生错误
     */
    public String generateGameHtml(String userInput) throws IOException {
        logger.info("开始生成游戏HTML，用户输入: {}", userInput);

        // 读取游戏生成提示模板
        String systemPrompt = readPromptFile("prompt/generate_html_game_prompt.txt");

        // 创建包含系统提示和用户输入的聊天请求
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setSystemPrompt(systemPrompt);
        chatRequest.setPrompt(userInput);

        // 调用Gemini API获取响应
        String response = chat(chatRequest);
        logger.info("游戏HTML生成完成，响应长度: {} 字符", response.length());

        return response;
    }
}
