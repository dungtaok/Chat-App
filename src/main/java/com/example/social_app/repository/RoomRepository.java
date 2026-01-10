package com.example.social_app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.social_app.model.ChatRoom;

public interface RoomRepository extends JpaRepository<ChatRoom, String> {

    // boolean existsByRoomIdAndUserId(String roomId, String userId);
    
}
