package com.example.social_app.dto.request;

import java.io.File;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    String bio;
    MultipartFile avatar;
    String lastName;
    String firstName;
    LocalDate dob;
}
