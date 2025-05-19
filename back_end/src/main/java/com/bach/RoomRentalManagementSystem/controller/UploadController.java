package com.bach.RoomRentalManagementSystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/upload")
public class UploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/images/";

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadSingleImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file == null || file.isEmpty()) {
            response.put("status", "error");
            response.put("message", "No file provided");
            return ResponseEntity.badRequest().body(response);
        }

        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            response.put("status", "error");
            response.put("message", "Could not create upload directory");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        try {
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFilename);
            String fileName = UUID.randomUUID() + fileExtension;

            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String imageUrl = "/api/image/" + fileName;

            response.put("status", "success");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Could not upload file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/image")
    public ResponseEntity<Map<String, Object>> deleteImage(@RequestParam("url") String imageUrl) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!imageUrl.contains("/api/image/")) {
                response.put("status", "error");
                response.put("message", "Invalid image URL");
                return ResponseEntity.badRequest().body(response);
            }

            String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            Path imagePath = Paths.get(UPLOAD_DIR + filename);

            if (Files.exists(imagePath)) {
                Files.delete(imagePath);
                response.put("status", "success");
                response.put("message", "Image deleted successfully");
            } else {
                response.put("status", "error");
                response.put("message", "Image not found");
            }

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Could not delete image");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        return dotIndex != -1 ? filename.substring(dotIndex) : "";
    }
}
