package com.example.social_app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.social_app.dto.request.MessageRequest;
import com.example.social_app.dto.request.RoomCreationRequest;
import com.example.social_app.dto.response.RoomCheckExistResponse;
import com.example.social_app.dto.response.RoomIdResponse;
import com.example.social_app.dto.response.RoomResponse;
import com.example.social_app.dto.response.UserResponse;
import com.example.social_app.model.ChatMessage;
import com.example.social_app.model.ChatRoom;
import com.example.social_app.model.User;
import com.example.social_app.repository.RoomRepository;
import com.example.social_app.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoomService {

    RoomRepository roomRepository;
    UserRepository  userRepository;
    ModelMapper modelMapper;

    public ChatRoom getById(String roomId) {
        return roomRepository.findById(roomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room id is not exists"));
    }

    public void saveRoom(ChatRoom room) {
        roomRepository.save(room);
    }
    
    public void createChatRoom(RoomCreationRequest request) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getName());
        chatRoom.setLastupdatedAt(LocalDateTime.now());

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
            message.setType(chatMessage.getType());
            response.add(message);
        }

        return response;
    }

    public RoomCheckExistResponse checkExistPrivateRoomByUserId(String id, HttpServletRequest httpServletRequest) {
        boolean exists = false;
        String currentUsername= httpServletRequest.getSession(true).getAttribute("username").toString();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
        List<ChatRoom>  rooms = currentUser.getChatRooms();

        String roomId = "";

        for(ChatRoom room : rooms){
            if(room.getUsers().size() == 2){
                List<User> users = room.getUsers();
                if(users.get(0).getId().equals(id) || users.get(1).getId().equals(id)){
                    exists = true;
                    roomId = room.getId();
                }
            }
        }

        return RoomCheckExistResponse.builder().exist(exists).roomId(roomId).build();
    }

    public RoomIdResponse createPrivateRoom(String userId, HttpServletRequest httpServletRequest) {
        String currentUsername= httpServletRequest.getSession(true).getAttribute("username").toString();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));
        User user = userRepository.findById(userId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Current user not found"));

        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(currentUser);
        
        String roomName = user.getFirstName() + " " + user.getLastName();

        ChatRoom chatRoom = ChatRoom.builder()
                    .users(users)
                    .name(roomName)
                    .avatar(user.getAvatar())
                    .roomType("PRIVATE")
                    .build();
        
        chatRoom = roomRepository.save(chatRoom);
        List<ChatRoom> chatRooms = user.getChatRooms();
        chatRooms.add(chatRoom);
        user.setChatRooms(chatRooms);
        userRepository.save(user);

        chatRooms = currentUser.getChatRooms();
        chatRooms.add(chatRoom);
        currentUser.setChatRooms(chatRooms);
        userRepository.save(currentUser);

        return RoomIdResponse.builder().roomId(chatRoom.getId()).build();
    }

    public void updateRoomTime(String roomId) {
        
        ChatRoom currentRoom = roomRepository.findById(roomId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdate = currentRoom.getLastupdatedAt();

        if(lastUpdate == null || lastUpdate.isBefore(now.minusSeconds(10))){
            currentRoom.setLastupdatedAt(now);
            roomRepository.save(currentRoom);
        }
    }

    public RoomResponse getRoomById(String id) {
        ChatRoom room = roomRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));

        return modelMapper.map(room, RoomResponse.class);
    }

    public List<UserResponse> getRoomMember(String id) {
        ChatRoom room = roomRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));

        List<User> users = room.getUsers();
        List<UserResponse> responses = new ArrayList<>();
        for(User user : users){
            UserResponse response = modelMapper.map(user, UserResponse.class);
            responses.add(response);
        }

        return responses;
    }

    public void addMemberIntoRoom(String id, String memberId) {
        ChatRoom room = roomRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));
        // user bị xóa khỏi room
        User user = userRepository.findById(memberId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not exist"));

        List<User> userList = room.getUsers();
        for(User user2 : userList){
            if(user2.getId().equals(user.getId())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already in the room");
            }
        }
        userList.add(user);
        room.setUsers(userList);
        room.setRoomType("PUBLIC");
        roomRepository.save(room);

        List<ChatRoom> rooms = user.getChatRooms();
        rooms.add(room);
        user.setChatRooms(rooms);
        userRepository.save(user);
    }

    public void deleteMemberFromRoom(String id, String memberId) {
        ChatRoom room = roomRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));
        User user = userRepository.findById(memberId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not exist"));

        // Không cần check tồn tại vì danh sách chỉ hiển thị các user có trong đoạn chat
        List<User> users = room.getUsers();
        List<ChatRoom> rooms = user.getChatRooms();

        List<User> userList = new ArrayList<>();
        List<ChatRoom> roomList = new ArrayList<>();

        for(User u : users){
            if(!u.getId().equals(user.getId())){
                userList.add(u);
            }
        }
        room.setUsers(userList);

        for(ChatRoom r : rooms){
            if(!r.getId().equals(room.getId())){
                roomList.add(r);
            }
        }
        user.setChatRooms(roomList);
        if(userList.size()==2){
            room.setRoomType("PRIVATE");
        }

        userRepository.save(user);
        roomRepository.save(room);
    }

    public RoomResponse getRoomDetail(String id, HttpServletRequest request) {
        ChatRoom room = roomRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));
        String roomType = room.getRoomType();
        if(roomType.equals("PUBLIC")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Thao tác không hợp lệ");
        }
        String username = (String) request.getSession(true).getAttribute("username");
        // User currentUser = userRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not exist"));
        String roomName = "";
        String avtUrl = "";
        
        for(User user : room.getUsers()){
            if(!username.equals(user.getUsername())){
                roomName = user.getFirstName() + " " + user.getLastName();
                avtUrl = user.getAvatar();
                break;
            }
        }

        return RoomResponse.builder().avatar(avtUrl).name(roomName).build();
    }

}
