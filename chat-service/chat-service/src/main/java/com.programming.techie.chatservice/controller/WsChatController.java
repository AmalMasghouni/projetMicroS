package com.programming.techie.chatservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Controller
public class WsChatController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String PRODUCT_SERVICE_URL = "http://product-service:8081/api/product/by-name/"; // adapte le port
    private final SimpMessagingTemplate messagingTemplate;

    public WsChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }
    @MessageMapping("chat.sendMessage")  // Maps messages sent to "chat.sendMessage" WebSocket destination
    @SendTo("/topic/public")  // Specifies that the return message will be sent to "/topic/public"
    public WsChatMessage sendMessage(@Payload WsChatMessage msg) {
        // Log the sender and content of the message for debugging
        System.out.println("Message received from " + msg.getSender() + ": " + msg.getContent());

        // Broadcast the message to all subscribers on the "/topic/public" topic
        return msg;
    }

    @MessageMapping("chat.addUser")
    public WsChatMessage addUser(@Payload WsChatMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", msg.getSender());

        System.out.println("User joined: " + msg.getSender());

        if (!"admin".equalsIgnoreCase(msg.getSender())) {
            try {
                String productName = msg.getContent();
                ResponseEntity<Product> response = restTemplate.getForEntity(PRODUCT_SERVICE_URL + productName, Product.class);

                String description;
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    description = "Description du produit '" + productName + "' : " + response.getBody().getDescription();
                } else {
                    description = "Produit non trouvé : " + productName;
                }

                // 💬 Construire un message comme si admin avait écrit
                WsChatMessage adminMsg = new WsChatMessage();
                adminMsg.setSender("admin");
                adminMsg.setContent(description);

                // 🔁 Envoyer ce message à tout le monde
                messagingTemplate.convertAndSend("/topic/chat", adminMsg);

            } catch (Exception e) {
                System.out.println("Erreur lors de la récupération du produit : " + e.getMessage());
            }
        }

    return msg;}
}
