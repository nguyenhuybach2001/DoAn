package com.bach.RoomRentalManagementSystem.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "recipient_id", nullable = false)
    private Long recipientId;

    @Column(name = "service_id")
    private Long serviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotiStatus status = NotiStatus.UNREAD;

    @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotiType type;
}
