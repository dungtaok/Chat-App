package com.example.social_app.controller.web;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.example.social_app.model.ChatMessage;

@Controller
public class ChatController {
    @MessageMapping("/chat.send") // Maps to /app/chat.send
    @SendTo("/public/messages") // Clients subscribe to /topic/messages
    public ChatMessage sendMessage(ChatMessage message) {
            message.setCreatedAt(LocalDateTime.now().toString());
        return message;
    }
}