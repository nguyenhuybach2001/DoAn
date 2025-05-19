package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.RoleName;
import com.bach.RoomRentalManagementSystem.service.UserManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.Map;

@Controller
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping("/auth/user")
    public ResponseEntity<Page<UserDto>> getUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) String identityNumber,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        RoleName roleEnum = (role != null) ? RoleName.valueOf(role.toUpperCase()) : null;

        Page<UserDto> userDtoPage = userManagementService.getUserByFilter(fullName, email, startDate, endDate, identityNumber, address, phoneNumber, roleEnum, page, size);
        return ResponseEntity.ok(userDtoPage);
    }

    @GetMapping("/admin/user/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            UserDto userDto = userManagementService.getUserById(userId);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }
}
