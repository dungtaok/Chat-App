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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
    public ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request, HttpServletRequest servletRequest){

        var authenticated = authenticationService.authenticate(request);

        if(authenticated.isAuthenticated()){

            HttpSession oldSession = servletRequest.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = servletRequest.getSession(true);
            session.setAttribute("username",request.getUsername());
            
            session.setAttribute("accessToken", authenticated.getToken());
        }

        return ApiResponse.<AuthenticationResponse>builder().data(authenticated).build();
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest servletRequest){
            HttpSession oldSession = servletRequest.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
    }

    @PostMapping("/introspect")
    @ResponseBody
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException{
        return ApiResponse.<IntrospectResponse>builder()
        .data(authenticationService.introspect(request))
        .build();
    }
    
}
