package com.example.social_app.controller.web;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.social_app.model.ChatRoom;
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

        User user = userService.getUserByUsername(username);
        List<ChatRoom> conversations = user.getChatRooms();
        model.addAttribute("userData", user);
        model.addAttribute("conversations", conversations);
        return "views/room";
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
