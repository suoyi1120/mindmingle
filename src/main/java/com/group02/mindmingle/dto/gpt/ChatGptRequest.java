package com.group02.mindmingle.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptRequest {
    private String model;
    private List<Message> messages;
    private double temperature = 0.7;
    private int max_tokens = 800;

    public static ChatGptRequest of(String model, String prompt) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("user", prompt));
        return new ChatGptRequest(model, messages, 0.7, 800);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}