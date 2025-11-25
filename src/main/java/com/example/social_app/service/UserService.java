package com.example.social_app.service;

import com.example.social_app.dto.request.UserCreationRequest;
import com.example.social_app.dto.response.ApiResponse;
import com.example.social_app.dto.response.UserResponse;
import com.example.social_app.model.User;
import com.example.social_app.repository.UserRepository;
import org.modelmapper.ModelMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    ModelMapper modelMapper;
    PasswordEncoder passwordEncoder;

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
}
