package com.group02.mindmingle.controller;

import com.group02.mindmingle.dto.gemini.ChatRequest;
import com.group02.mindmingle.dto.gemini.Contents;
import com.group02.mindmingle.dto.gemini.Parts;
import com.group02.mindmingle.dto.gemini.Prompt;
import com.group02.mindmingle.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/gemini")
public class GeminiRestController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/generate")
    public ResponseEntity<Object> getResponseFromGemini(@RequestBody Parts parts) {
        Prompt prompt = new Prompt();
        Contents contents = new Contents();

        List<Contents> contentsList = new ArrayList<>();
        List<Parts> partsList = new ArrayList<>();

        partsList.add(parts);
        contents.setParts(partsList);
        contentsList.add(contents);
        prompt.setContents(contentsList);

        return new ResponseEntity<>(geminiService.callGeminiAPI(prompt), HttpStatus.OK);
    }

    @PostMapping("/generate-text")
    public ResponseEntity<String> getTextResponseFromGemini(@RequestBody Parts parts) {
        Prompt prompt = new Prompt();
        Contents contents = new Contents();

        List<Contents> contentsList = new ArrayList<>();
        List<Parts> partsList = new ArrayList<>();

        partsList.add(parts);
        contents.setParts(partsList);
        contentsList.add(contents);
        prompt.setContents(contentsList);

        String response = geminiService.getResponseText(prompt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chatWithGemini(@RequestBody ChatRequest chatRequest) {
        Prompt prompt = chatRequest.toPrompt();
        String response = geminiService.getResponseText(prompt);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}