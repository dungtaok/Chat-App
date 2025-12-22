package com.example.social_app.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.dto.request.RoomCreationRequest;
import com.example.social_app.model.ChatMessage;
import com.example.social_app.model.ChatRoom;
import com.example.social_app.repository.RoomRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomRepository roomRepository;
    
    public void createChatRoom(RoomCreationRequest request) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getName());

        roomRepository.save(chatRoom);
    }

    public List<MessageRequest> getAllMessage(String id) {
        ChatRoom room = roomRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room id is not exists"));
        List<ChatMessage> chatMessages = room.getMessages();
        List<MessageRequest> response = new ArrayList<>();
        for(ChatMessage chatMessage : chatMessages){
            MessageRequest message = new MessageRequest();
            message.setContent(chatMessage.getContent());
            message.setCreatedAt(chatMessage.getCreatedAt());
            message.setSender(chatMessage.getSender().getUsername());
            message.setRecipient(chatMessage.getRecipient().getId());

            response.add(message);
        }

        return response;
    }
}
