package com.bach.RoomRentalManagementSystem.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bach.RoomRentalManagementSystem.dto.LoginDto;
import com.bach.RoomRentalManagementSystem.dto.RegisterDto;
import com.bach.RoomRentalManagementSystem.service.UserService;

@RestController
public class UserController {

	@Autowired
    private UserService userService;
	
	@PostMapping("/admin/register")
	public ResponseEntity<Map<String, String>> register(@RequestBody RegisterDto registerDto) {
	    Map<String, String> response = userService.register(registerDto);
	    return ResponseEntity.ok(response);
	}
	
	@PostMapping("/auth/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Map<String, String> tokens = userService.authenticate(loginDto);
        return ResponseEntity.ok(tokens);
    }
	
	@PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refreshToken");

        String accessToken = userService.refreshAccessToken(refreshToken);

        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/user/info")
    public ResponseEntity<User> getUser(@RequestParam("accessToken") String accessToken) {
        User user = (User) userService.getCurrentUser(accessToken);
        return ResponseEntity.ok(user);
    }
}
