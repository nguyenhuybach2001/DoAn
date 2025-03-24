package com.bach.RoomRentalManagementSystem.service;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.accessibility.AccessibleKeyBinding;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bach.RoomRentalManagementSystem.dto.LoginDto;
import com.bach.RoomRentalManagementSystem.dto.RegisterDto;
import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.RoleName;
import com.bach.RoomRentalManagementSystem.model.User;
import com.bach.RoomRentalManagementSystem.model.UserToken;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserTokenRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoleRepository;
import com.bach.RoomRentalManagementSystem.security.JwtUtilities;
import com.bach.RoomRentalManagementSystem.serviceImpl.ImplUserService;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements ImplUserService{
	
	@Value("${fe.host}")
    private String feHost;
	
	private final AuthenticationManager authenticationManager ;
    private final IUserRepository iUserRepository ;
    private final IRoleRepository iRoleRepository ;
    private final IUserTokenRepository iUserTokenRepository;
    private final PasswordEncoder passwordEncoder ;
    private final JwtUtilities jwtUtilities ;
    private final MailService mailService;
	
	
    @Override
    public Role saveRole(Role role) {
        return iRoleRepository.save(role);
    }

    @Override
    public User saverUser(User user) {
        return iUserRepository.save(user);
    }

    @Override
    public Map<String, String> register(RegisterDto registerDto) {
        Map<String, String> response = new HashMap<>();

        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
            response.put("message", "Email is already taken!");
            response.put("status", "fail");
        } else {
        	Role role = iRoleRepository.findByRoleName(registerDto.getRoleName());

            if (role == null) {
                response.put("message", "Invalid role provided");
                response.put("status", "fail");
            } else {
                User user = new User();
                user.setEmail(registerDto.getEmail());
                user.setFullName(registerDto.getFullName());
                String password = registerDto.getPassword();
                user.setPasswordHash(passwordEncoder.encode(password));

                user.setRole(role);

                iUserRepository.save(user);

                String registerToken = jwtUtilities.generateRegisterToken(registerDto.getEmail(), user.getPasswordHash(), "REGISTER_CONTEXT");
                String fromMail = "bachbom27@gmail.com";
                String toMail = registerDto.getEmail();
                String subject = "Xác nhận tài khoản";
                String template = "templates/validate.html";

                Map<String, String> parameters = new HashMap<>();
                parameters.put("name", registerDto.getFullName());
                parameters.put("pass", password);

                try {
                    mailService.sendHtmlEmail(fromMail, toMail, subject, template, parameters);
                    response.put("message", "Registration successful. Please check your email to verify your account.");
                    response.put("status", "success");
                } catch (Exception e) {
                    response.put("message", "Failed to send verification email: " + e.getMessage());
                    response.put("status", "fail");
                }
            }
        }
        return response;
    }

    @Override
    public Map<String, String> authenticate(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = iUserRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        String accessKey =  generateUniqueString(50);
        String refreshKey = generateUniqueString(100);

        String accessToken = jwtUtilities.generateAccessToken(user.getUsername(), user.getRole().getRoleName(), accessKey, "ACCESS_CONTEXT");
        String refreshToken = jwtUtilities.generateRefreshToken(user.getUsername(), accessKey, refreshKey, "REFRESH_CONTEXT");
        
        Date accessTokenExpiration = (Date.from(Instant.now().plus(jwtUtilities.getJwtExpiration(), ChronoUnit.MILLIS)));

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setAccessKey(accessKey);
        userToken.setRefreshKey(refreshKey);
        userToken.setExpiresAt(accessTokenExpiration);
        userToken.setCreatedAt(Date.from(Instant.now()));
        iUserTokenRepository.save(userToken);

       
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }
    
    public static String generateUniqueString(int length) {
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        return uniqueString.length() > length ? uniqueString.substring(0, length) : uniqueString;
    }
    
    public String refreshAccessToken(String refreshToken) {
        if (!jwtUtilities.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtilities.extractUsername(refreshToken);
        String refreshKey = jwtUtilities.extractClaim(refreshToken, claims -> claims.get("key", String.class));

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserToken userToken = iUserTokenRepository.findTopByRefreshKeyAndAccessKeyAndUserIdAndExpiresAtAfter(
                refreshKey, user.getUserId(), LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("User token not found"));

        if (jwtUtilities.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token has expired");
        }
        
        String accessKey =  generateUniqueString(50);

        userToken.setAccessKey(accessKey);  
        iUserTokenRepository.save(userToken); 
        
        String accessToken = jwtUtilities.generateAccessToken(user.getUsername(), user.getRole().getRoleName(), accessKey, "ACCESS_CONTEXT");

        return accessToken;
    }
    
    @Override
    public User getCurrentUser(String accessToken) {
    	
    	String accessKey = jwtUtilities.extractClaim(accessToken, claims -> claims.get("key", String.class));
    	
        UserToken userToken = iUserTokenRepository.findByAccessKey(accessKey)
                .orElseThrow(() -> new RuntimeException("Access token not found"));
        
        if (jwtUtilities.isTokenExpired(accessToken)) {
            throw new RuntimeException("Access token has expired");
        }

        if (jwtUtilities.isTokenExpired(accessToken)) {
            throw new RuntimeException("Access token has expired");
        }

        User user = userToken.getUser();

        return user;
    }

}

