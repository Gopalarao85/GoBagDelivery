package com.gobag.gobag_service.service;

import com.gobag.gobag_service.model.WhatsAppLog;
import com.gobag.gobag_service.repository.WhatsAppLogRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class WhatsAppService {

    private final WhatsAppLogRepository logRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String WHATSAPP_API_URL = "https://graph.facebook.com/v22.0/553308501205548/messages";

    private final String ACCESS_TOKEN = "EAAHgejM8slMBO2oKKOEpp4sUdjcpYnORtr7hiQbiC3m8dby5nPdbnlv2t1rGUjZBBL1jCZBk1juCAwXSoCbkFHy6t2nD0XwmJZBoEw56oE7ENpBjp5yl9ZB5ylUHdoggwVF15ZAZAD5porrsfOQazprwtAMqZBunPhVhQM6zguHASTDoolq1XihZBBoM3TE17p5h9gZDZD"; // Replace with a valid access token

    public WhatsAppService(WhatsAppLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public String sendWhatsAppMessage(String customerNumber, String message) {
        if (!customerNumber.startsWith("+")) {
            throw new IllegalArgumentException("Invalid phone number format. Must be in E.164 format (e.g., +919876543210)");
        }

        String requestPayload = String.format(
            "{ \"messaging_product\": \"whatsapp\", \"recipient_type\": \"individual\", \"to\": \"%s\", \"type\": \"text\", \"text\": {\"body\": \"%s\"} }",
            customerNumber, message
        );

        System.out.println("Request Payload: " + requestPayload); // Debugging

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(WHATSAPP_API_URL, HttpMethod.POST, requestEntity, String.class);

            // Save API request/response in DB
            WhatsAppLog log = new WhatsAppLog(requestPayload, response.getBody(), LocalDateTime.now());
            logRepository.save(log);

            return response.getBody();
        } catch (HttpClientErrorException e) {
            System.err.println("WhatsApp API Error: " + e.getResponseBodyAsString());
            return "WhatsApp API error: " + e.getMessage();
        }
    }
}
