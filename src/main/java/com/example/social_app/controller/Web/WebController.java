package com.example.social_app.controller.Web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class WebController {

    @GetMapping("/messages")
    public String showMessageBox() {
        return "views/room.html";
    }

    @GetMapping("/chat")
    public String showChatDemo() {
        return "views/index.html";
    }
    

    @GetMapping("/")
    public String redirectToLogin() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "views/login.html";
    }
    
}
