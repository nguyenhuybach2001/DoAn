package com.bach.RoomRentalManagementSystem.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;

@RestController
@RequestMapping("/image")
@CrossOrigin(origins = "http://localhost:2001") // hoặc "*" nếu cần mở toàn bộ
public class ImageController {

    private static final String IMAGE_DIR = System.getProperty("user.dir") + "/uploads/images/";

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(IMAGE_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // Tự động nhận dạng loại ảnh
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
