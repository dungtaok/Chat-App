package com.example.social_app.controller.web;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.model.ChatMessage;
import com.example.social_app.service.MessageService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatController {


    // công cụ để gửi tin nhắn từ server -> client (1 cách chủ động)
    // có thể gửi public, hoặc private
    SimpMessagingTemplate simpMessagingTemplate; 
    MessageService messageService;

    @MessageMapping("/chat.send") // Maps to /app/chat.send
    @SendTo("/public/messages") // Clients subscribe to /topic/messages
    public MessageRequest sendMessage(MessageRequest message) {
            message.setCreatedAt(LocalDateTime.now().toString());
        return message;
    }


    // Client gửi lên Server
    @MessageMapping("/chat.send/{roomId}")
    // @DestinationVariable giống như @PathVariable -> tách id từ endpoint
    public void handleChatMessage(@DestinationVariable String roomId, MessageRequest chatMessage){
        chatMessage.setCreatedAt(LocalDateTime.now().toString());

        messageService.createNewMessage(chatMessage);

        simpMessagingTemplate.convertAndSend("/queue/room/" + roomId, chatMessage);
    }

    //Server gửi lại tin nhắn về kênh mà các Clients subcribe
}