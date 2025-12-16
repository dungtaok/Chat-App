package com.example.social_app.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketHandler extends TextWebSocketHandler {

    private final List<WebSocketSession> sessions = new ArrayList<>(); // danh sách người online

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // khi có người kết nối mới
        sessions.add(session);
    }

    @Override
    // xử lí khi có tin nhắn đến
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { 
        for (WebSocketSession s : sessions) {
            // gửi broadcast cho tất cả user đang kết nối đến server
            if (s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // khi có người thoát -> xóa ra khỏi 
        sessions.remove(session);
    }
}