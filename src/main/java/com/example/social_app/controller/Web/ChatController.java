package com.example.social_app.controller.web;

import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.service.MessageService;
import com.example.social_app.service.MessageTransferService;
import com.example.social_app.service.RoomService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ChatController {


    // công cụ để gửi tin nhắn từ server -> client (1 cách chủ động)
    // có thể gửi public, hoặc private
    // SimpMessagingTemplate simpMessagingTemplate; 
    MessageService messageService;
    MessageTransferService messageTransferService;
    RoomService roomService;

    @MessageMapping("/chat.send") // Maps to /app/chat.send
    @SendTo("/public/messages") // Clients subscribe to /topic/messages
    public MessageRequest sendMessage(MessageRequest message) {
            message.setCreatedAt(LocalDateTime.now().toString());
        return message;
    }


    // Client gửi lên Server
    @MessageMapping("/chat.send/{roomId}")
    // @DestinationVariable giống như @PathVariable -> tách id từ endpoint
    public void handleChatMessage(@DestinationVariable String roomId, @Payload MessageRequest chatMessage) throws Exception{
        chatMessage.setCreatedAt(LocalDateTime.now().toString());

        try {
            messageService.createNewMessage(chatMessage);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        // simpMessagingTemplate.convertAndSend("/queue/room/" + roomId, chatMessage);
        // ChatRoom room = roomService.getById(roomId);
        // room.setLastupdatedAt(LocalDateTime.now());
        // roomService.saveRoom(room);

        roomService.updateRoomTime(roomId);
        messageTransferService.sendMessageToChannel(roomId, chatMessage);
    }

    @MessageMapping("/chat.sendFile/{roomId}")
    // @DestinationVariable giống như @PathVariable -> tách id từ endpoint
    public void handleChatFileMessage(@DestinationVariable String roomId,@Payload MessageRequest chatMessage) throws Exception{
        chatMessage.setCreatedAt(LocalDateTime.now().toString());
        // var fileMsg = 
        try {
            messageService.createNewMessage(chatMessage);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        // simpMessagingTemplate.convertAndSend("/queue/room/" + roomId, chatMessage);
        // ChatRoom room = roomService.getById(roomId);
        // room.setLastupdatedAt(LocalDateTime.now());
        // roomService.saveRoom(room);

        roomService.updateRoomTime(roomId);
        messageTransferService.sendMessageToChannel(roomId, chatMessage);
    }
}