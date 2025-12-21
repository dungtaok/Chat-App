package com.example.social_app.controller;


import com.example.social_app.dto.request.UserCreationRequest;
import com.example.social_app.dto.request.UserUpdateRequest;
import com.example.social_app.dto.response.ApiResponse;
import com.example.social_app.dto.response.UserResponse;
import com.example.social_app.model.User;
import com.example.social_app.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserController {

    UserService userService;

    @PostMapping("/users")
    @ResponseBody
    public ApiResponse<Void> createNewUser(@RequestBody UserCreationRequest request){
        return userService.createNewUser(request);
    }

    @GetMapping("/users")
    public List<UserResponse> getAllUser(){
        return userService.getALlUser();
    }

    @PostMapping("/user/{id}")
    public void updateUser(@PathVariable(name = "id") String id  ,@RequestBody UserUpdateRequest request){
        userService.updateUser(id, request);
    }

    
    @PostMapping("/users/{username}")
    public void updateUserByUsername(@PathVariable(name = "username") String username  ,@ModelAttribute UserUpdateRequest request){
        userService.updateUserByUsername(username, request);
    }


    @GetMapping("/users/avatar/{username}")
    public String getImageBase64(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return user.getAvatar();
    }
    

}
