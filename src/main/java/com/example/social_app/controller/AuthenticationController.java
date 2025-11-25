package com.example.social_app.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;

import com.example.social_app.dto.request.AuthenticationRequest;
import com.example.social_app.dto.request.IntrospectRequest;
import com.example.social_app.dto.response.ApiResponse;
import com.example.social_app.dto.response.AuthenticationResponse;
import com.example.social_app.dto.response.IntrospectResponse;
import com.example.social_app.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    @ResponseBody
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return ApiResponse.<AuthenticationResponse>builder()
        .data(authenticationService.authenticate(request))
        .build();
    }

    @PostMapping("/introspect")
    @ResponseBody
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException{
        return ApiResponse.<IntrospectResponse>builder()
        .data(authenticationService.introspect(request))
        .build();
    }
    
}
