package com.gobag.gobag_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    @Value("${whatsapp.phone.number.id}")
    private String phoneNumberId;

    @Value("${whatsapp.access.token}")
    private String accessToken;

    @Value("${whatsapp.verify.token}")
    private String verifyToken;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Store user-selected restaurant
    private final Map<String, String> userSelectedRestaurant = new HashMap<>();

    // ✅ Webhook Verification
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge) {

        return ("subscribe".equals(mode) && verifyToken.equals(token))
                ? ResponseEntity.ok(challenge)
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid Token");
    }

    // ✅ Handle Incoming WhatsApp Messages
    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("Received Payload: " + payload);

        List<Map<String, Object>> entryList = (List<Map<String, Object>>) payload.get("entry");
        if (entryList != null && !entryList.isEmpty()) {
            Map<String, Object> firstEntry = entryList.get(0);
            List<Map<String, Object>> changesList = (List<Map<String, Object>>) firstEntry.get("changes");

            if (changesList != null && !changesList.isEmpty()) {
                Map<String, Object> firstChange = changesList.get(0);
                Map<String, Object> value = (Map<String, Object>) firstChange.get("value");

                List<Map<String, Object>> messages = (List<Map<String, Object>>) value.get("messages");
                if (messages != null && !messages.isEmpty()) {
                    processIncomingMessage(messages.get(0));
                }
            }
        }

        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    private void processIncomingMessage(Map<String, Object> message) {
        String from = (String) message.get("from");
        String messageType = (String) message.get("type");
        System.out.println("messageType====>: " + messageType);
        if ("text".equals(messageType)) {
            String userText = (String) ((Map<String, Object>) message.get("text")).get("body");
            if ("hi".equalsIgnoreCase(userText.trim()) || "hello".equalsIgnoreCase(userText.trim())) {
                sendRestaurantList(from);
            }
        } else if ("interactive".equals(messageType)) {
            handleInteractiveMessage(from, (Map<String, Object>) message.get("interactive"));
        }
    }

    private void handleInteractiveMessage(String from, Map<String, Object> interactiveObj) {
    	  System.out.println("button_reply===> " + interactiveObj.containsKey("button_reply"));
    	  System.out.println("list_reply===> " + interactiveObj.containsKey("list_reply"));
    	if (interactiveObj.containsKey("list_reply")) {
            // ✅ Restaurant Selected → Store and Fetch Menu
            Map<String, Object> buttonReply = (Map<String, Object>) interactiveObj.get("list_reply");
            String selectedRestaurant = (String) buttonReply.get("title");

            // 🔥 Fix: Store the selected restaurant in memory
            userSelectedRestaurant.put(from, selectedRestaurant);
            System.out.println("✅ Restaurant stored successfully for user [" + from + "]: " + selectedRestaurant);

            // Fetch and send the menu
            fetchMenu(from, selectedRestaurant);

        } else if (interactiveObj.containsKey("button_reply")) {
            // ✅ Menu Item Selected → Confirm Order
            Map<String, Object> listReply = (Map<String, Object>) interactiveObj.get("list_reply");
            String selectedItem = (String) listReply.get("title");

            System.out.println("✅ Menu item selected by user [" + from + "]: " + selectedItem);

            // 🔥 Fix: Retrieve stored restaurant before confirming order
            String storedRestaurant = userSelectedRestaurant.get(from);

            if (storedRestaurant != null) {
                System.out.println("✅ Retrieved stored restaurant for user [" + from + "]: " + storedRestaurant);
                sendOrderConfirmation(from, storedRestaurant, selectedItem);
            } else {
                System.err.println("❌ ERROR: No restaurant found for user [" + from + "]. UserSelectedRestaurant Map: " + userSelectedRestaurant);
                sendTextMessage(from, "⚠️ Error: No restaurant selected. Please start again by typing *Hi*.");
            }
        }
    }


    // ✅ Step 1: Send Welcome Message with Restaurant List
    private void sendRestaurantList(String to) {
        List<Map<String, Object>> rows = List.of(
                createRow("res_1", "Hotel A", "Best Biryani in town!"),
                createRow("res_2", "Hotel B", "Authentic South Indian Meals."),
                createRow("res_3", "Hotel C", "Tasty Fast Food and Snacks.")
        );

        Map<String, Object> payload = createInteractiveListMessage(
                to, "Welcome to GoBag! Please select a restaurant:", 
                "Select Restaurant", "Available Restaurants", rows
        );

        sendWhatsAppMessage(payload);
    }

    // ✅ Step 2: Fetch Menu for Selected Restaurant
    private void fetchMenu(String customerId, String restaurant) {
        List<Map<String, Object>> menuItems = getProductsForRestaurant(restaurant);

        if (menuItems.isEmpty()) {
            sendTextMessage(customerId, "⚠️ Sorry, we couldn't find a menu for " + restaurant + ".");
            return;
        }

        System.out.println("✅ Sending menu for [" + customerId + "]: " + restaurant);

        Map<String, Object> payload = createInteractiveListMessage(
                customerId, "📜 Here is the menu for " + restaurant + ":", 
                "View Menu", restaurant + " Menu", menuItems
        );
        sendWhatsAppMessage(payload);
    }

    // ✅ Step 3: Order Confirmation
    private void sendOrderConfirmation(String recipient, String restaurant, String item) {
        sendTextMessage(recipient, "✅ Order Confirmed!\nRestaurant: " + restaurant + "\nItem: " + item + "\n\nThank you for choosing GoBag! 🎉");
    }

    // ✅ Send WhatsApp Message
    private void sendWhatsAppMessage(Map<String, Object> payload) {
        try {
            String url = "https://graph.facebook.com/v17.0/" + phoneNumberId + "/messages";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            System.out.println("✅ WhatsApp API Response: " + response.getBody());
        } catch (Exception e) {
            System.err.println("❌ Error sending WhatsApp message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ✅ Send Text Message
    private void sendTextMessage(String to, String message) {
        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "text",
                "text", Map.of("body", message)
        );
        sendWhatsAppMessage(payload);
    }
 // ✅ Create an Interactive List Message for WhatsApp
    private Map<String, Object> createInteractiveListMessage(String to, String bodyText, String buttonText, String sectionTitle, List<Map<String, Object>> rows) {
        return Map.of(
                "messaging_product", "whatsapp",
                "recipient_type", "individual",
                "to", to,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "list",
                        "body", Map.of("text", bodyText),
                        "action", Map.of(
                                "button", buttonText,
                                "sections", List.of(Map.of("title", sectionTitle, "rows", rows))
                        )
                )
        );
    }
    
 // ✅ Fetch Menu Items Based on Selected Restaurant
    private List<Map<String, Object>> getProductsForRestaurant(String restaurant) {
    	 System.out.println("✅ WhatsApp API Response gopal====: " + restaurant.toLowerCase());
    	return switch (restaurant.toLowerCase()) {
            case "hotel a" -> List.of(
                    createRow("item_1", "🍛 Chicken Biryani", "Best biryani in town"),
                    createRow("item_2", "🍗 Mutton Curry", "Spicy and delicious"),
                    createRow("item_3", "🥤 Soft Drink", "Cool and refreshing")
            );
            case "hotel b" -> List.of(
                    createRow("item_4", "🍕 Veg Pizza", "Cheese-loaded pizza"),
                    createRow("item_5", "🍔 Veg Burger", "Crispy and juicy"),
                    createRow("item_6", "🍹 Cold Coffee", "Refreshing and creamy")
            );
            case "hotel c" -> List.of(
                    createRow("item_7", "🍣 Sushi Platter", "Authentic Japanese sushi"),
                    createRow("item_8", "🥗 Greek Salad", "Healthy and fresh"),
                    createRow("item_9", "🍨 Chocolate Ice Cream", "Rich and creamy")
            );
            default -> Collections.emptyList();
        };
    }

 // ✅ Create a Row for WhatsApp List (Used for Restaurant List & Menu)
    private Map<String, Object> createRow(String id, String title, String description) {
        return Map.of(
                "id", id,
                "title", title,
                "description", description
        );
    }

}
