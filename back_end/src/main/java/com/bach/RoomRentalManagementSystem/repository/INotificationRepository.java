package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRecipientId(Long recipientId);

    @Query("""
            SELECT n.senderId FROM Notification n
            JOIN User u ON n.senderId = u.id
            JOIN u.role r
            WHERE n.serviceId = :serviceId
            AND r.roleName = 'STAFF'
            """)
    Optional<Long> findSenderIdByServiceIdAndSenderId(Long serviceId);

}
