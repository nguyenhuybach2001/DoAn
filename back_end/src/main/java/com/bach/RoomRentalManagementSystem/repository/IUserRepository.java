package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.AssetInRoom;
import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.RoleName;
import com.bach.RoomRentalManagementSystem.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IUserRepository extends JpaRepository<User,Long>{

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.email =:email AND u.role.roleName = 'STAFF'")
    Optional<User> findStaffByEmail(String email);

    Optional<User> findByEmail(String email);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.role.roleName IN ('ADMIN', 'STAFF')")
    List<User> findUsersAdminAndStaff();

    User findByRole(Role role);
}
