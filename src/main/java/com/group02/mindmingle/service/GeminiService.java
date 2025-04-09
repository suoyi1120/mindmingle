package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.gemini.ChatRequest;
import com.group02.mindmingle.dto.gemini.GeminiResponse;
import com.group02.mindmingle.dto.gemini.Prompt;
import com.group02.mindmingle.dto.gemini.Contents;
import com.group02.mindmingle.dto.gemini.Parts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public GeminiResponse callGeminiAPI(Prompt prompt) {
        HttpEntity<Prompt> requestEntity = new HttpEntity<>(prompt);
        System.out.println("Sending request to Gemini API with prompt: " + prompt);
        ResponseEntity<GeminiResponse> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                GeminiResponse.class);
        return response.getBody();
    }

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

    // 新增方法，从Parts构建Prompt
    public Prompt buildPromptFromParts(Parts parts) {
        Prompt prompt = new Prompt();
        Contents contents = new Contents();

        List<Contents> contentsList = new ArrayList<>();
        List<Parts> partsList = new ArrayList<>();

        partsList.add(parts);
        contents.setParts(partsList);
        contentsList.add(contents);
        prompt.setContents(contentsList);

        return prompt;
    }

    // 生成调用API的响应
    public GeminiResponse generateResponse(Parts parts) {
        Prompt prompt = buildPromptFromParts(parts);
        return callGeminiAPI(prompt);
    }

    // 生成文本响应
    public String generateTextResponse(Parts parts) {
        Prompt prompt = buildPromptFromParts(parts);
        return getResponseText(prompt);
    }

    // 聊天功能
    public String chat(ChatRequest chatRequest) {
        Prompt prompt = chatRequest.toPrompt();
        return getResponseText(prompt);
    }
}
