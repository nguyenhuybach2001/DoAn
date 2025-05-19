package com.bach.RoomRentalManagementSystem.controller;

import java.util.Map;

import com.bach.RoomRentalManagementSystem.dto.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.bach.RoomRentalManagementSystem.service.UserService;

@RestController
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/admin/create-account")
    public ResponseEntity<Map<String, String>> createAccount(@Valid @RequestBody RegisterDto request) {
        try {
            Map<String, String> response = userService.createAccount(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while creating the account"));
        }
    }

//    @PostMapping("/register")
//    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDto registerDto) {
//        try {
//            Map<String, String> response = userService.register(registerDto);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An error occurred while registering the customer"));
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            Map<String, String> tokens = userService.authenticate(loginDto);
            return ResponseEntity.ok(tokens);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody String email) {
        try {
            Map<String, String> response = userService.requestForgotPassword(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordDto resetPasswordDto) {
        try {
            Map<String, String> response = userService.resetPassword(resetPasswordDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/auth/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            Map<String, String> response = userService.changePassword(changePasswordDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
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

    @PutMapping("/auth/me")
    public ResponseEntity<?> updateUserInfo(@RequestBody UpdateUserDto updateUserDto) {

            Map<String, String> response = userService.updateUserInfo(updateUserDto);
            return ResponseEntity.ok(response);

    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> getUser() {
        try {
            UserDto user = userService.getCurrentUser();
            return ResponseEntity.ok(user);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token has expired"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or missing token"));
        }
    }

//    @PostMapping("/active/account")
//    public ResponseEntity<Map<String, String>> activeAccount(@RequestBody ActiveDto activeAccountRequest) {
//        try {
//            Map<String, String> response = userService.activeAcc(activeAccountRequest);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("error", "Invalid token"));
//        }
//    }
}
