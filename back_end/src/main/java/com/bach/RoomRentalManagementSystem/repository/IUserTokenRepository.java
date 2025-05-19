package com.bach.RoomRentalManagementSystem.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bach.RoomRentalManagementSystem.model.User;
import com.bach.RoomRentalManagementSystem.model.UserToken;

public interface IUserTokenRepository extends JpaRepository<UserToken, Long> {

    Boolean existsByUser_IdAndRefreshKey(Long userId, String refreshKey);

}
