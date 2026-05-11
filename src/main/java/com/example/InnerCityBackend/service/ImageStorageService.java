package com.example.InnerCityBackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String storeImage(MultipartFile file, String subDirectory) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, subDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Return the URL path
            return "/uploads/" + subDirectory + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            // Extract path from URL (remove leading slash if present)
            String relativePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;
            Path filePath = Paths.get(relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}