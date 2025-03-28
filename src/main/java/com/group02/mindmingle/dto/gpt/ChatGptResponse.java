package com.group02.mindmingle.dto.gpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatGptResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private Usage usage;
    private List<Choice> choices;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        private long prompt_tokens;
        private long completion_tokens;
        private long total_tokens;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Choice {
        private ChatGptRequest.Message message;
        private String finish_reason;
        private int index;
    }
}