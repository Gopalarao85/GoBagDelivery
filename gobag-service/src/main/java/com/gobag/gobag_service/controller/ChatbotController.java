package com.gobag.gobag_service.controller;

import com.gobag.gobag_service.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> getChatbotResponse(@RequestBody Map<String, String> request) {
        String userMessage = request.get("userMessage");  // Extract user input
        String botResponse = chatbotService.getResponse(userMessage); // Process message

        // Create JSON response
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", botResponse);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.ok(response); // Return JSON response
    }
}
