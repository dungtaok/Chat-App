package com.example.social_app.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    String sender;
    String content;
    String createdAt;
    String destination;
}
