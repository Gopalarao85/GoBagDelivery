package com.gobag.gobag_service.service;

import com.gobag.gobag_service.model.ChatHistory;
import com.gobag.gobag_service.repository.ChatHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatHistoryService {
    
    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public void saveChat(String customerNumber, String message, String response) {
        ChatHistory chat = new ChatHistory();
        chat.setCustomerNumber(customerNumber);
        chat.setMessageType("INCOMING");  // or "OUTGOING" based on logic
        chat.setMessageContent(message + " | Response: " + response);
        chat.setTimestamp(LocalDateTime.now());

        chatHistoryRepository.save(chat);
    }
}
