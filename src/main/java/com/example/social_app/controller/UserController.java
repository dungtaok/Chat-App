package com.example.social_app.controller;


import com.example.social_app.dto.request.UserCreationRequest;
import com.example.social_app.dto.response.ApiResponse;
import com.example.social_app.dto.response.UserResponse;
import com.example.social_app.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
