package com.example.social_app.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService {

    public String encodeImageToBase64(MultipartFile file) throws IOException {
        byte[] fileContent = file.getBytes();
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public void decodeBase64ToImage(String base64String, String outputPath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        Files.write(new File(outputPath).toPath(), decodedBytes);
    }
    
}
