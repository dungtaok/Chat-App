package com.example.social_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.social_app.model.ChatMessage;

public interface MessageRepository extends JpaRepository<ChatMessage, String> {
        
}
