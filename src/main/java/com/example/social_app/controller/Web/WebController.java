package com.example.social_app.controller.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties.Apiversion.Use;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.social_app.RoomType;
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
        User userDetail = userService.getUserByUsername(username);
        List<ChatRoom> conversations = userDetail.getChatRooms();
        List<ChatRoom> conversationList = new ArrayList<>();    

        for(ChatRoom room : conversations){
            String roomName = "";
            String avtUrl = "";
            if(room.getRoomType().equals("PRIVATE")){
                for(User user : room.getUsers()){
                    if(user.getId() != userDetail.getId()){
                        roomName = user.getFirstName() + " " + user.getLastName();
                        avtUrl = user.getAvatar();
                    }
                }
            }else{
                for(User user : room.getUsers()){
                    if(user.getId() != userDetail.getId()){
                        if(avtUrl!=null && avtUrl.length()!=0){
                            avtUrl = user.getAvatar();
                        }
                        roomName += ", " + user.getFirstName() + " " + user.getLastName();
                    }
                }
                roomName = roomName.substring(2);
            }

            if(roomName.length() > 35){
                roomName = roomName.substring(0,33);
                roomName += "...";
            }

            room.setName(roomName);
            room.setAvatar(avtUrl);
            conversationList.add((room));
        }

        model.addAttribute("userData", userDetail); // user đang đăng nhập    
        model.addAttribute("conversations", conversationList); // các đoạn chat hiện của user
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
