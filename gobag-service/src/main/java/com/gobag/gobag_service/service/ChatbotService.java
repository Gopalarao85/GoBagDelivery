package com.gobag.gobag_service.service;

import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

    public String getResponse(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return "I'm here to assist you! Please ask me something.";
        }
        
        switch (userMessage.toLowerCase()) {
            case "hello":
                return "Hello! How can I help you today?";
            case "how are you?":
                return "I'm just a bot, but I'm doing great! What about you?";
            case "what is gobag?":
                return "GoBag is a local delivery service in Jaggayyapet. We provide food delivery and more!";
            case "bye":
                return "Goodbye! Have a great day!";
            default:
                return "I'm not sure how to respond to that. Can you ask something else?";
        }
    }
}
