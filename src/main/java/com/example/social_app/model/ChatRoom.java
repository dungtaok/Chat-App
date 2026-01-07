package com.example.social_app.model;

import java.time.LocalDateTime;
import java.util.List;

import com.example.social_app.RoomType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "chat_room")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String name;
    LocalDateTime createdAt;
    String theme;

    @ManyToMany(mappedBy = "chatRooms")
    List<User> users;

    @OneToMany(mappedBy = "recipient")
    @OrderBy("createdAt ASC")
    List<ChatMessage> messages;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    String avatar;

    LocalDateTime lastupdatedAt;

    String roomType;

}
