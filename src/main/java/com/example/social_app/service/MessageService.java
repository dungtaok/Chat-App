package com.example.social_app.service;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.model.ChatMessage;
import com.example.social_app.repository.MessageRepository;
import com.example.social_app.repository.RoomRepository;
import com.example.social_app.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageService {

    MessageRepository messageRepository;
    ModelMapper modelMapper;
    UserRepository userRepository;
    RoomRepository roomRepository;

    public void createNewMessage(MessageRequest request){
        ChatMessage message = modelMapper.map(request, ChatMessage.class);
        message.setSender(userRepository.findByUsername(request.getSender()).get());
        message.setRecipient(roomRepository.findById(request.getRecipient()).get());
        messageRepository.save(message);
    }

    
}
