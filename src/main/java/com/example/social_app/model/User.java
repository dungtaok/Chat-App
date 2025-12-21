package com.example.social_app.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "user")
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String username;
    String password;
    String firstName;
    String lastName;
    Boolean status;
    LocalDate dob;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    String avatar;

    String bio;

    @ManyToMany()
    @JoinTable(
        name = "user_friend",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name="friend_id")
    )
    List<User> friends;
    // List<Post> posts;

    @OneToMany(mappedBy = "user")
    List<Message> messages;

    @ManyToMany()
    @JoinTable(name = "user_chat_room",
        joinColumns = @JoinColumn(name="room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    List<ChatRoom> chatRooms;

}
