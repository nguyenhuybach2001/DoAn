package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.NotificationDto;
import com.bach.RoomRentalManagementSystem.model.NotiType;
import com.bach.RoomRentalManagementSystem.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/auth/notification")
    public ResponseEntity<?> sendNotification(@RequestBody NotificationDto notificationDto) {
        try {
            Long sender_id = notificationDto.getSenderId();
            Long service_id = notificationDto.getServiceId();
            String message = notificationDto.getMessage();
            Long recipientId = notificationDto.getRecipientId();
            NotiType type = NotiType.valueOf(String.valueOf(notificationDto.getType()));
            Map<String, String> response = notificationService.sendNotification(sender_id, recipientId, message, service_id, type);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/auth/notification/{id}")
    public ResponseEntity<?> readNotification(@PathVariable Long id) {
        try {
            Map<String, String> response = notificationService.checkReadNotification(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/auth/notification")
    public ResponseEntity<?> deleteNotification(@RequestBody List<Long> ids) {
        try {
            Map<String, String> response = notificationService.deleteNotification(ids);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/auth/notification")
    public ResponseEntity<?> getAllNotification(@RequestParam Long user_id) {
        try {
            List<NotificationDto> response = notificationService.getAllNotificationsByRecipientId(user_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
