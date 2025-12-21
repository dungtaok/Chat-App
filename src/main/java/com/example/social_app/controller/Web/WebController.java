package com.example.social_app.controller.web;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.social_app.model.User;
import com.example.social_app.service.UserService;

import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Controller
public class WebController {

    UserService userService;

    @GetMapping("/messages")
    public String showMessageBox(HttpSession session ,Model model) {
        String username = (String) session.getAttribute("username");
        if(username == null){
            return "redirect:/login";
        }
        String token = (String) session.getAttribute("accessToken");

        User user = userService.getUserByUsername(username);
        model.addAttribute("userData", user);
        return "views/room";
    }

    @GetMapping("/chat")
    public String showChatDemo() {
        return "views/index"; 
    }
    

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(HttpSession session ,Model model) {
        if(session.getAttribute("username") != null){
            return "redirect:/messages";
        }
        return "views/login";
    }
    
}
