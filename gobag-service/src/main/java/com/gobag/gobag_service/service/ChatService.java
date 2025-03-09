package com.gobag.gobag_service.service;

import com.gobag.gobag_service.model.ChatHistory;
import com.gobag.gobag_service.model.WhatsAppLog;
import com.gobag.gobag_service.repository.ChatHistoryRepository;
import com.gobag.gobag_service.repository.WhatsAppLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final WhatsAppLogRepository whatsAppLogRepository;

    public ChatService(ChatHistoryRepository chatHistoryRepository, WhatsAppLogRepository whatsAppLogRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.whatsAppLogRepository = whatsAppLogRepository;
    }

    public void saveChatHistory(String customerNumber, String messageType, String messageContent) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCustomerNumber(customerNumber);
        chatHistory.setMessageType(messageType);
        chatHistory.setMessageContent(messageContent);
        chatHistory.setTimestamp(LocalDateTime.now());
        
        chatHistoryRepository.save(chatHistory);
    }

    public void saveWhatsAppLog(String requestType, String phoneNumber, String requestPayload, String responsePayload) {
        WhatsAppLog log = new WhatsAppLog();
        log.setRequestType(requestType);
        log.setPhoneNumber(phoneNumber);
        log.setRequestPayload(requestPayload);
        log.setResponsePayload(responsePayload);
        log.setTimestamp(LocalDateTime.now());

        whatsAppLogRepository.save(log);
    }
}
