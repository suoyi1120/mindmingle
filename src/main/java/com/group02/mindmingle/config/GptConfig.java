package com.group02.mindmingle.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class GptConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.timeout:30}")
    private int timeout;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openaiApiKey, Duration.ofSeconds(timeout));
    }
}