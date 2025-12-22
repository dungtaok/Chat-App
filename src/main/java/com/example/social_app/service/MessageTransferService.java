package com.example.social_app.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.example.social_app.dto.request.MessageRequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageTransferService {

    SimpMessagingTemplate simpMessagingTemplate;

    public void sendMessageToChannel(String roomId, MessageRequest chatMessage){
        simpMessagingTemplate.convertAndSend("/queue/room/"+roomId, chatMessage);
    }
    
}
