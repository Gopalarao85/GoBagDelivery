package com.gobag.gobag_service.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final String PHONE_NUMBER_ID = "553308501205548";
    private final String ACCESS_TOKEN = "EAAHgejM8slMBO2oKKOEpp4sUdjcpYnORtr7hiQbiC3m8dby5nPdbnlv2t1rGUjZBBL1jCZBk1juCAwXSoCbkFHy6t2nD0XwmJZBoEw56oE7ENpBjp5yl9ZB5ylUHdoggwVF15ZAZAD5porrsfOQazprwtAMqZBunPhVhQM6zguHASTDoolq1XihZBBoM3TE17p5h9gZDZD"; // Replace with a valid access token
    private final String VERIFY_TOKEN = "gobag123"; // Your custom token

    // ✅ Webhook Verification Endpoint
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {
        
        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(token)) {
            return ResponseEntity.ok(challenge); // Respond with challenge
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
        }
    }

    // ✅ Handle Incoming WhatsApp Messages
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Received Payload: " + payload);
        List<Map<String, Object>> messages = (List<Map<String, Object>>) payload.get("messages");

        if (messages != null && !messages.isEmpty()) {
            Map<String, Object> message = messages.get(0);
            String from = (String) message.get("from"); // User's WhatsApp Number
            String text = (String) ((Map<String, Object>) message.get("text")).get("body"); // Received Message

            if ("hi".equalsIgnoreCase(text.trim())) {
                sendRestaurantList(from); // Send restaurant list
            } else {
                sendTextMessage(from, "Invalid response. Please try again.");
            }
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    // ✅ Send Restaurant List to User
    private void sendRestaurantList(String to) {
        String url = "https://graph.facebook.com/v17.0/" + PHONE_NUMBER_ID + "/messages";
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", to);
        payload.put("type", "interactive");

        Map<String, Object> interactive = new HashMap<>();
        interactive.put("type", "list");

        Map<String, Object> body = new HashMap<>();
        body.put("text", "Welcome to GoBag! Please select a restaurant:");
        interactive.put("body", body);

        Map<String, Object> action = new HashMap<>();
        action.put("button", "Select Restaurant");

        List<Map<String, Object>> sections = new ArrayList<>();
        Map<String, Object> section = new HashMap<>();
        section.put("title", "Available Restaurants");

        List<Map<String, Object>> rows = new ArrayList<>();
        rows.add(createRow("res_1", "Hotel A", "Best Biryani in town!"));
        rows.add(createRow("res_2", "Hotel B", "Authentic South Indian Meals."));
        rows.add(createRow("res_3", "Hotel C", "Tasty Fast Food and Snacks."));

        section.put("rows", rows);
        sections.add(section);
        action.put("sections", sections);
        
        interactive.put("action", action);
        payload.put("interactive", interactive);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        System.out.println("Sending Payload: " + entity.getBody());
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("WhatsApp API Response: " + response.getBody());
    }

    // ✅ Send Text Message
    private void sendTextMessage(String to, String message) {
        String url = "https://graph.facebook.com/v17.0/" + PHONE_NUMBER_ID + "/messages";
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("recipient_type", "individual");
        payload.put("to", to);
        payload.put("type", "text");

        Map<String, Object> text = new HashMap<>();
        text.put("body", message);
        payload.put("text", text);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(ACCESS_TOKEN);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        System.out.println("WhatsApp API Response: " + response.getBody());
    }

    // ✅ Create a List Row for WhatsApp Interactive List
    private Map<String, Object> createRow(String id, String title, String description) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", id);
        row.put("title", title);
        row.put("description", description);
        return row;
    }
}
