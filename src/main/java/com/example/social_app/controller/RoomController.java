package com.example.social_app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.dto.request.RoomCreationRequest;
import com.example.social_app.dto.response.RoomCheckExistResponse;
import com.example.social_app.dto.response.RoomIdResponse;
import com.example.social_app.dto.response.RoomResponse;
import com.example.social_app.dto.response.UserResponse;
import com.example.social_app.model.ChatMessage;
import com.example.social_app.service.RoomService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


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

    @PostMapping("/private")
    public RoomIdResponse createPrivateRoom(@RequestBody String userId, HttpServletRequest request) {
        return roomService.createPrivateRoom(userId, request);
    }
    

    @GetMapping("/{id}")
    public RoomResponse getRoomById(@PathVariable(name = "id") String id) {
        return roomService.getRoomById(id);
    }

    @PostMapping("/{id}")
    public void addMemberIntoRoom(@PathVariable(name = "id")String id, @RequestBody String memberId) {
        roomService.addMemberIntoRoom(id, memberId);
    }

    @DeleteMapping("/{id}")
    public void deleteMemberFromRoom(@PathVariable(name = "id")String id, @RequestBody String memberId){
        roomService.deleteMemberFromRoom(id, memberId);
    }   
    
    @GetMapping("/room-messages/{id}")
    public List<MessageRequest> getAllMessage(@PathVariable("id") String id){
        return roomService.getAllMessage(id);
    }

    @GetMapping("/exist-private/{id}")
    public RoomCheckExistResponse checkExistPrivateRoomByUserId(@PathVariable("id") String id, HttpServletRequest httpServletRequest){
        return roomService.checkExistPrivateRoomByUserId(id, httpServletRequest);
    }

    @GetMapping("/member/{id}")
    public List<UserResponse> getRoomMember(@PathVariable(name = "id")String id) {
        return roomService.getRoomMember(id);
    }

    @GetMapping("/detail/{id}")
    public RoomResponse getRoomDetail(@PathVariable(name = "id") String id, HttpServletRequest request){
        return roomService.getRoomDetail(id, request);
    }
    
    // @GetMapping("/user/{id}")
    // public List<RoomResponse> getAllConversationByUserId(@PathVariable(name = "id")String id) {
    //     return roomService.getAllRoomByUserId(id);
    // }
    
    
}
