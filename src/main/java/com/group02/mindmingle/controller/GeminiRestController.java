package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.gemini.ChatRequest;
import com.group02.mindmingle.dto.gemini.Parts;
import com.group02.mindmingle.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiRestController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<Object> getResponseFromGemini(@RequestBody Parts parts) {
        return new ResponseEntity<>(geminiService.generateResponse(parts), HttpStatus.OK);
    }

    @PostMapping("/generate-text")
    public ResponseEntity<String> getTextResponseFromGemini(@RequestBody Parts parts) {
        String response = geminiService.generateTextResponse(parts);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithGemini(@RequestBody ChatRequest chatRequest) {
        String response = geminiService.chat(chatRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}