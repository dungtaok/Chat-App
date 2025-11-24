package com.example.social_app.controller;


import com.example.social_app.dto.request.UserCreationRequest;
import com.example.social_app.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    UserService userService;

    @PostMapping("/users")
    public void createNewUser(@RequestBody UserCreationRequest request){
        userService.createNewUser(request);
    }

}
