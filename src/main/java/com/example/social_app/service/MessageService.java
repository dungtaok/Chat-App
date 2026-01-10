package com.example.social_app.service;


import java.io.IOException;
import java.util.Base64;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public void createNewMessage(MessageRequest request) throws IOException{
        ChatMessage message = modelMapper.map(request, ChatMessage.class);
        message.setSender(userRepository.findByUsername(request.getSender()).get());
        message.setRecipient(roomRepository.findById(request.getRecipient()).get());

        // if(message.getType().equals("FILE")){
        //     byte[] bytes = file.getBytes();
        //     byte[] compressBytes = imageService.compress(bytes);
        //     String fileString = Base64.getEncoder().encodeToString(compressBytes);

        //     message.setContent(fileString);
        // }

        messageRepository.save(message);
    }

    
}
