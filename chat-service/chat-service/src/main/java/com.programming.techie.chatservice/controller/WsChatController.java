package com.programming.techie.chatservice.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
public class WsChatController {
    @Controller
    public class ChatController {

        @MessageMapping("/chat.sendMessage")
        @SendTo("/topic/public")
        public WsChatMessage sendMessage(@Payload WsChatMessage message, Principal principal) {
            if (principal != null) {
                message.setSender(principal.getName());
            } else {
                message.setSender("Anonymous");
            }
            System.out.println("Message from " + message.getSender() + ": " + message.getContent());
            return message;
        }

        @MessageMapping("/chat.addUser")
        @SendTo("/topic/public")
        public WsChatMessage addUser(@Payload WsChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
            String username = message.getSender();
            headerAccessor.getSessionAttributes().put("username", username);
            System.out.println("User joined: " + username);
            return message;
        }
    }



    /*@MessageMapping("chat.sendMessage")  // Maps messages sent to "chat.sendMessage" WebSocket destination
    @SendTo("/topic/public")  // Specifies that the return message will be sent to "/topic/public"
    public WsChatMessage sendMessage(@Payload WsChatMessage msg) {
        // Log the sender and content of the message for debugging
        System.out.println("Message received from " + msg.getSender() + ": " + msg.getContent());

        // Broadcast the message to all subscribers on the "/topic/public" topic
        return msg;
    }*/
    /* noooon@MessageMapping("chat.sendMessage")
    @SendTo("/topic/public")
    public WsChatMessage sendMessage(@Payload WsChatMessage msg, Principal principal) {
        if (principal == null) {
            System.out.println("Principal is NULL !");
        } else
        {
            System.out.println("helloooooo");
        System.out.println("User from JWT: " + principal.getName()); // Nom d'utilisateur depuis le token
        msg.setSender(principal.getName()); // Tu peux override ici si tu veux
        return msg;}

        return msg;
    }*/

   /* @MessageMapping("chat.addUser")  // Maps messages sent to "chat.addUser" WebSocket destination
    @SendTo("/topic/chat")  // Specifies that the return message will be sent to "/topic/chat"
    public WsChatMessage addUser(@Payload WsChatMessage msg, SimpMessageHeaderAccessor headerAccessor) {
        // Store the username in the WebSocket session attributes
        headerAccessor.getSessionAttributes().put("username", msg.getSender());

        // Log when a user joins the chat
        System.out.println("User joined: " + msg.getSender());

        // Broadcast the user join event to all subscribers on the "/topic/chat" topic
        return msg;
    }*/
   /*@MessageMapping("chat.addUser")
   @SendTo("/topic/chat")
   public WsChatMessage addUser(@Payload WsChatMessage msg, SimpMessageHeaderAccessor headerAccessor) {
       Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

       if (sessionAttributes == null) {
           System.err.println("Session attributes is null!");
           throw new IllegalStateException("Session attributes are null!");
       }

       sessionAttributes.put("username", msg.getSender());
       System.out.println("User joined: " + msg.getSender());
       return msg;
   }*/
   /*noooo@MessageMapping("chat.addUser")
   @SendTo("/topic/chat")
   public WsChatMessage addUser(@Payload WsChatMessage msg, SimpMessageHeaderAccessor headerAccessor) {
       System.out.println("[addUser] Début méthode addUser");

       Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

       if (sessionAttributes == null) {
           System.out.println("[addUser] SessionAttributes est null, création d'une nouvelle map");
           sessionAttributes = new HashMap<>();
           headerAccessor.setSessionAttributes(sessionAttributes);
       }

       String sender = msg.getSender();
       if (sender == null || sender.isEmpty()) {
           System.err.println("[addUser] Sender est null ou vide !");
           throw new IllegalArgumentException("Sender is null or empty");
       }

       sessionAttributes.put("username", sender);
       System.out.println("[addUser] Utilisateur ajouté en session : " + sender);

       System.out.println("[addUser] Fin méthode addUser");
       return msg;
   }
*/



}
