package com.bach.RoomRentalManagementSystem.serviceImpl;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import com.bach.RoomRentalManagementSystem.dto.LoginDto;
import com.bach.RoomRentalManagementSystem.dto.RegisterDto;
import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.User;


public interface ImplUserService {

	Map<String, String> authenticate(LoginDto loginDto);
	Map<String, String> register (RegisterDto registerDto);
	Role saveRole(Role role);

	User saverUser (User user) ;
	String refreshAccessToken(String refreshToken);
	User getCurrentUser(String accessToken);
	
}
