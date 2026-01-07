package com.example.social_app.service;

import com.example.social_app.dto.request.AuthenticationRequest;
import com.example.social_app.dto.request.UserCreationRequest;
import com.example.social_app.dto.request.UserUpdateRequest;
import com.example.social_app.dto.response.ApiResponse;
import com.example.social_app.dto.response.UserResponse;
import com.example.social_app.model.ChatRoom;
import com.example.social_app.model.User;
import com.example.social_app.repository.RoomRepository;
import com.example.social_app.repository.UserRepository;

import jakarta.persistence.PrePersist;
import jakarta.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;
    ImageService imageService;
    RoomRepository roomRepository;

    AuthenticationService authenticationService;

    public ApiResponse<Void> createNewUser(UserCreationRequest request) {
        ApiResponse<Void> response = new ApiResponse<>();

        if(userRepository.existsByUsername(request.getUsername())){
            response.setMessage("Username was existed");
            response.setCode(HttpStatus.CONFLICT.value());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username was existed");
        }
        User user = modelMapper.map(request, User.class);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(true);

        userRepository.save(user);

        return response;
    }

    public List<UserResponse> getALlUser() {
        List<User> listUser = userRepository.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for( User  user : listUser){
            responses.add(modelMapper.map(user, UserResponse.class));
        }
        return responses;
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).get();
    }

    public UserResponse getUserById(String id){
        User user =  userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists"));

        UserResponse response = modelMapper.map(user, UserResponse.class);

        return response;
    }

    public void updateUser(String id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists"));

        if( request.getFirstName()!=null && request.getFirstName().trim() != ""){
            user.setFirstName(request.getFirstName());
        }
        if( request.getLastName()!=null && request.getLastName().trim() != ""){
            user.setLastName(request.getLastName());
        }
        if( request.getBio() !=null && request.getBio().trim()!=""){
            user.setBio(request.getBio());
        }
        if(request.getDob() != null){
            user.setDob(request.getDob());
        }

        if(request.getAvatar() != null && !request.getAvatar().isEmpty()){
            String avtBase64 = null;
            try {
                avtBase64 = imageService.encodeImageToBase64(request.getAvatar());
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setAvatar(avtBase64);
        }

        userRepository.save(user);
    }

    public void updateUserByUsername(String username, UserUpdateRequest request) {
         User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not exists"));

        if( request.getFirstName()!=null && request.getFirstName().trim() != ""){
            user.setFirstName(request.getFirstName());
        }
        if( request.getLastName()!=null && request.getLastName().trim() != ""){
            user.setLastName(request.getLastName());
        }
        if( request.getBio() !=null && request.getBio().trim()!=""){
            user.setBio(request.getBio());
        }
        if(request.getDob() != null){
            user.setDob(request.getDob());
        }

        if(request.getAvatar() != null && !request.getAvatar().isEmpty()){
            String avtBase64 = null;
            try {
                avtBase64 = imageService.encodeImageToBase64(request.getAvatar());
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setAvatar(avtBase64);
        }

        userRepository.save(user);
    }

    public List<ChatRoom> getAllConversation(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not exists"));
        
        return user.getChatRooms();
    }

    public void deleteRoom(String id, String roomId) {
        User user = userRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not exist"));
        ChatRoom room = roomRepository.findById(roomId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room is not exist"));

        List<ChatRoom> newRooms = new ArrayList<>();
        List<User> newUsers = new ArrayList<>();

        for(ChatRoom chatRoom : user.getChatRooms()){
            if(!chatRoom.getId().equals(roomId)){
                newRooms.add(chatRoom);
            }
        }

        for(User u : room.getUsers()){
            if(!u.getId().equals(id)){
                newUsers.add(u);
            }
        }

        user.setChatRooms(newRooms);
        room.setUsers(newUsers);

        userRepository.save(user);
        roomRepository.save(room);
    }

    public boolean checkPassword(String pw, HttpServletRequest request) {
        String username = (String) request.getSession(true).getAttribute("username");
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder().username(username).password(pw).build();
        boolean authenticated = authenticationService.authenticate(authenticationRequest).isAuthenticated();
        return authenticated;
    }

    public void changePassword(String pw, HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute("username");
        User user = userRepository.findByUsername(username).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User is not exist"));
        user.setPassword(passwordEncoder.encode(pw));
        userRepository.save(user);
    }

}
