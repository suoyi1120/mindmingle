package com.group02.mindmingle.service;

import com.group02.mindmingle.dto.gemini.GeminiResponse;
import com.group02.mindmingle.dto.gemini.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
}
