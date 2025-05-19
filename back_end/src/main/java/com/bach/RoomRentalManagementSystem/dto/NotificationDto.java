package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.NotiStatus;
import com.bach.RoomRentalManagementSystem.model.NotiType;
import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class NotificationDto {

    private Long id;
    private String message;
    private Long senderId;
    private Long recipientId;
    private NotiStatus status;
    private NotiType type;
    private Timestamp timestamp;
    private Long serviceId;
}
