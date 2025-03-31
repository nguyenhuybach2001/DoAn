package com.bach.RoomRentalManagementSystem.controller;

import java.util.Map;

import com.bach.RoomRentalManagementSystem.dto.ActiveDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bach.RoomRentalManagementSystem.dto.LoginDto;
import com.bach.RoomRentalManagementSystem.dto.RegisterDto;
import com.bach.RoomRentalManagementSystem.service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/admin/create-account")
    public ResponseEntity<Map<String, String>> createAccount(@RequestBody RegisterDto request) {
        try {
            Map<String, String> response = userService.createAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while creating the account"));
        }
    }

    @PostMapping("/customer/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterDto registerDto) {
        try {
            Map<String, String> response = userService.register(registerDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while registering the customer"));
        }
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        try {
            Map<String, String> tokens = userService.authenticate(loginDto);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        try {
            String refreshToken = refreshTokenRequest.get("refreshToken");
            Map<String, String> response = userService.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid refresh token"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getUser() {
        try {
            UserDto user = userService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body((UserDto) Map.of("error", "Sai"));
        }

    }

    @PostMapping("/active/account")
    public ResponseEntity<Map<String,String>> activeAccount(@RequestBody ActiveDto activeAccountRequest) {
        try {
            Map<String, String> response = userService.activeAcc(activeAccountRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid token"));
        }
    }
}
