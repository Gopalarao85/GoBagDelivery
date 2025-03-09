package com.gobag.gobag_service.controller;

import com.gobag.gobag_service.model.ChatHistory;
import com.gobag.gobag_service.service.ChatHistoryService;
import com.gobag.gobag_service.service.WhatsAppService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final WhatsAppService whatsAppService;
    private final ChatHistoryService chatHistoryService;

    public ChatController(WhatsAppService whatsAppService, ChatHistoryService chatHistoryService) {
        this.whatsAppService = whatsAppService;
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping("/receive")
    public String receiveMessage(@RequestBody Map<String, String> requestBody) {
        String customerNumber = requestBody.get("customerNumber"); // Extract customerNumber
        String message = requestBody.get("message"); // Extract message
        String response;

        if (message.equalsIgnoreCase("hi")) {
            response = "Welcome to GoBag! Select a restaurant:\n1️⃣ Hotel A\n2️⃣ Hotel B";
        } else if (message.equals("1")) {
            response = "Hotel A Menu:\n🍔 Burger\n🍕 Pizza\n🥤 Coke";
        } else {
            response = "Invalid response. Please try again.";
        }

    
        // Save chat history
        chatHistoryService.saveChat(customerNumber, message, response);

        // Send response to WhatsApp
        whatsAppService.sendWhatsAppMessage(customerNumber, response);
        return response;
    }
}
