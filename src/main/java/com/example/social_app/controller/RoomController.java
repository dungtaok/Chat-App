package com.example.social_app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.dto.request.RoomCreationRequest;
import com.example.social_app.model.ChatMessage;
import com.example.social_app.service.RoomService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/chat-room")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class RoomController {
    RoomService roomService;
    
    @PostMapping()
    public void createChatRoom(@RequestBody RoomCreationRequest request) {
        roomService.createChatRoom(request);
    }

    @GetMapping("/room-messages/{id}")
    public List<MessageRequest> getAllMessage(@PathVariable("id") String id){
        return roomService.getAllMessage(id);
    }
    
}
