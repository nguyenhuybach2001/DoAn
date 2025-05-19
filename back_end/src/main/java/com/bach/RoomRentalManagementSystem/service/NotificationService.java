package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.NotificationDto;
import com.bach.RoomRentalManagementSystem.model.NotiStatus;
import com.bach.RoomRentalManagementSystem.model.Notification;
import com.bach.RoomRentalManagementSystem.repository.INotificationRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.bach.RoomRentalManagementSystem.model.NotiType;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final INotificationRepository iNotificationRepository;
    private final IUserRepository iUserRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Map<String,String> sendNotification(
            Long senderId,
            Long recipientId,
            String message,
            Long serviceId,
            NotiType type
    ) {
        Map<String,String> response = new HashMap<>();

        if (!iUserRepository.existsById(recipientId)) {
            response.put("message", "Recipient not found");
            response.put("status", "error");
            return response;
        }
        Notification notification = new Notification();
        notification.setSenderId(senderId);
        notification.setServiceId(serviceId);
        notification.setMessage(message);
        notification.setRecipientId(recipientId);
        notification.setType(type);
        notification.setTimestamp(Timestamp.from(Instant.now()));

        iNotificationRepository.save(notification);

        messagingTemplate.convertAndSend(
                "/topic/" + type.toString().toLowerCase() + recipientId,
                notification
        );

        response.put("message", "Notification sent successfully");
        response.put("status", "success");
        return response;
    }


    private boolean recipientDoesNotExist(Long recipientId) {
        return !iUserRepository.existsById(recipientId);
    }

    public Map<String, String> checkReadNotification(Long id) {
        Map<String, String> response = new HashMap<>();

        Notification notification = iNotificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification not found!"));

        if (notification.getStatus() == NotiStatus.READ) {
            response.put("message", "Notification is already marked as read");
            response.put("status", "info");
            return response;
        }

        notification.setStatus(NotiStatus.READ);
        iNotificationRepository.save(notification);

        response.put("message", "Notification was read by user");
        response.put("status", "success");
        return response;
    }


    public Map<String, String> deleteNotification(List<Long> ids) {
        Map<String, String> response = new HashMap<>();

        List<Notification> notifications = iNotificationRepository.findAllById(ids);
        if (notifications.size() != ids.size()) {
            response.put("message", "Some notifications were not found");
            response.put("status", "error");
            return response;
        }

        iNotificationRepository.deleteAll(notifications);

        response.put("message", "Notifications deleted successfully");
        response.put("status", "success");
        return response;
    }

    public List<NotificationDto> getAllNotificationsByRecipientId(Long recipientId) {
        List<Notification> notifications = iNotificationRepository.findAllByRecipientId(recipientId);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        for (Notification noti : notifications) {
            NotificationDto notificationDto = NotificationDto.builder()
                    .id(noti.getId())
                    .message(noti.getMessage())
                    .senderId(noti.getSenderId())
                    .recipientId(noti.getRecipientId())
                    .status(noti.getStatus())
                    .type(noti.getType())
                    .serviceId(noti.getServiceId())
                    .timestamp(noti.getTimestamp())
                    .build();

            notificationDtos.add(notificationDto);
        }
        return notificationDtos;
    }
}
