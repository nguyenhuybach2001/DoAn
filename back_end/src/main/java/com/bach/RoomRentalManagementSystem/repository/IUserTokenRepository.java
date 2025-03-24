package com.bach.RoomRentalManagementSystem.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bach.RoomRentalManagementSystem.model.User;
import com.bach.RoomRentalManagementSystem.model.UserToken;

public interface IUserTokenRepository extends JpaRepository<UserToken, Long>{

	@Query("SELECT ut FROM UserToken ut JOIN ut.user u WHERE u.email = :email ORDER BY ut.createdAt DESC")
    Optional<UserToken> findTopByUserEmailOrderByCreatedAtDesc(@Param("email") String email);
	
	Optional<UserToken> findByUser(User user);

	Optional<UserToken> findByAccessKey(String accessToken);
	
	@Query("SELECT ut FROM UserToken ut WHERE ut.refreshKey = :refreshKey " +
            "AND ut.user.id = :userId " +
            "AND ut.expiresAt > :currentDate " +
            "ORDER BY ut.createdAt DESC")
    Optional<UserToken> findTopByRefreshKeyAndAccessKeyAndUserIdAndExpiresAtAfter(
            @Param("refreshKey") String refreshKey,
            @Param("userId") Long userId,
            @Param("currentDate") LocalDateTime currentDate
    );
}
