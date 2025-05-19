package com.bach.RoomRentalManagementSystem.service;

import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.util.*;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.bach.RoomRentalManagementSystem.dto.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.RoleName;
import com.bach.RoomRentalManagementSystem.model.User;
import com.bach.RoomRentalManagementSystem.model.UserToken;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserTokenRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoleRepository;
import com.bach.RoomRentalManagementSystem.security.JwtUtilities;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    @Value("${fe.host}")
    private String feHost;

    @Value("${maijet.from}")
    private String fromMail;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refreshExpiration}")
    private Long refreshTokenExpiration;

    private final AuthenticationManager authenticationManager;
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IUserTokenRepository iUserTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;
    private final MailService mailService;

    public Role saveRole(Role role) {
        return iRoleRepository.save(role);
    }

    public User saverUser(User user) {
        return iUserRepository.save(user);
    }

    // service đăng ký cho khách
//    @Override
//    public Map<String, String> register(RegisterDto registerDto) {
//        Map<String, String> response = new HashMap<>();
//
//        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
//            response.put("error", "Email is already taken!");
//        } else {
//            User user = new User();
//            user.setEmail(registerDto.getEmail());
//            user.setFullName(registerDto.getFullName());
//            user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
//            user.setRole(iRoleRepository.findByRoleName(RoleName.valueOf("CUSTOMER")));
//            LocalDateTime createdAt = getCurrentTime();
//            user.setCreatedAt(createdAt);
//
//            iUserRepository.save(user);
//
//            Map<String, String> registerParams = new HashMap<>();
//            registerParams.put("email", registerDto.getEmail());
//            //registerParams.put("createdAt", user.getCreatedAt().toString());
//
//            String registerToken = jwtUtilities.generateSimpleToken(registerParams, "REGISTER_CONTEXT");
//            String toMail = registerDto.getEmail();
//            String subject = "Xác nhận tài khoản";
//            String template = "templates/validate.html";
//            String link = String.format("%s?token=%s&email=%s", feHost, registerToken, toMail);
//            Map<String, String> parameters = new HashMap<>();
//            parameters.put("name", registerDto.getFullName());
//            parameters.put("link", link);
//
//            try {
//                mailService.sendHtmlEmail(fromMail, toMail, subject, template, parameters);
//                response.put("error", "Registration successful.");
//            } catch (Exception e) {
//                response.put("error", "Failed to send verification email: " + e.getMessage());
//            }
//        }
//        return response;
//    }

//    public Map<String, String> activeAcc(ActiveDto activeDto) {
//        Map<String, String> response = new HashMap<>();
//
//        User user = iUserRepository.findByEmail(activeDto.getEmail())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (user.getIsActive()) {
//            response.put("error", "Account is already active!");
//            return response;
//        }
//
//        Map<String, String> registerParams = new HashMap<>();
//        registerParams.put("email", user.getEmail());
//
//        String registerToken = jwtUtilities.generateSimpleToken(registerParams, "REGISTER_CONTEXT");
//
//        if (!registerToken.equals(activeDto.getToken())) {
//            response.put("error", "Token is invalid!");
//            return response;
//        }
//
//
//        user.setIsActive(true);
//        iUserRepository.save(user);
//
//        response.put("success", "Account activated successfully.");
//        return response;
//    }

    public Map<String, String> authenticate(LoginDto loginDto) {
        Map<String, String> response = new HashMap<>();
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = iUserRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return getToken(user);
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Invalid email or password");
        }
    }

    public Map<String, String> getToken(User user) {
        String accessKey = UUID.randomUUID().toString();
        String refreshKey = UUID.randomUUID().toString();

        Map<String, String> accessParams = new HashMap<>();
        accessParams.put("email", user.getUsername());
        accessParams.put("key", accessKey);

        String accessToken = jwtUtilities.generateToken(accessParams, "ACCESS_CONTEXT", jwtExpiration);

        Map<String, String> refreshParams = new HashMap<>();
        refreshParams.put("email", user.getUsername());
        refreshParams.put("key", refreshKey);

        String refreshToken = jwtUtilities.generateToken(refreshParams, "REFRESH_CONTEXT", refreshTokenExpiration);

        LocalDateTime accessTokenExpiration = getCurrentTime().plus(jwtExpiration, ChronoUnit.MILLIS);

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setAccessKey(accessKey);
        userToken.setRefreshKey(refreshKey);
        userToken.setExpiresAt(accessTokenExpiration);
        userToken.setCreatedAt(getCurrentTime());
        iUserTokenRepository.save(userToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("expiredDate", accessTokenExpiration.toString());

        return tokens;
    }

    public Map<String, String> requestForgotPassword(String email) {
        Map<String, String> response = new HashMap<>();

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Map<String, String> forgotPassParams = new HashMap<>();
        forgotPassParams.put("email", user.getEmail());

        String forgotPassToken = jwtUtilities.generateSimpleToken(forgotPassParams, "FORGOT_PASSWORD_CONTEXT");

        String subject = "Đặt lại mật khẩu";
        String template = "templates/ResetPassword.html";
        String link = String.format("%s?token=%s&email=%s", feHost, forgotPassToken, email);
        Map<String, String> parameters = new HashMap<>();
        parameters.put("link", link);

        try {
            mailService.sendHtmlEmail(fromMail, email, subject, template, parameters);
            response.put("success", "Send mail successful.");
        } catch (Exception e) {
            response.put("error", "Failed to send verification email: " + e.getMessage());
        }
        return response;
    }

    public Map<String, String> resetPassword(ResetPasswordDto resetPasswordDto) {
        Map<String, String> response = new HashMap<>();

        User user = iUserRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> forgotPassParams = new HashMap<>();
        forgotPassParams.put("email", user.getEmail());

        String forgotPassToken = jwtUtilities.generateSimpleToken(forgotPassParams, "FORGOT_PASSWORD_CONTEXT");

        if (!forgotPassToken.equals(resetPasswordDto.getToken())) {
            response.put("error", "Token is invalid!");
            return response;
        }

        user.setPasswordHash(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        iUserRepository.save(user);

        response.put("success", "Change password successfully.");
        return response;
    }


    public Map<String, String> changePassword(ChangePasswordDto changePasswordDto) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("User not found or not authenticated");
        }

        String email = authentication.getName();

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentPasswordHash = user.getPasswordHash();

        if (!passwordEncoder.matches(changePasswordDto.getOldPassword(), currentPasswordHash)) {
            throw new RuntimeException("Old password is incorrect!");
        }

        if (changePasswordDto.getOldPassword().equals(changePasswordDto.getNewPassword())) {
            throw new RuntimeException("New password should be different from the old password!");
        }

        String newPasswordHash = passwordEncoder.encode(changePasswordDto.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        iUserRepository.save(user);


        response.put("success", "Change password successfully.");
        return response;
    }

    // Admin tạo account staff
    public Map<String, String> createAccount(RegisterDto registerDto) {
        Map<String, String> response = new HashMap<>();

        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
            response.put("message", "Email is already registered!");
            return response;
        }
        String randomPassword = generateRandomPassword(8);

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setFullName(registerDto.getFullName());
        user.setPasswordHash(passwordEncoder.encode(randomPassword));
        user.setIsPasswordChanged(false);
        user.setCreatedAt(getCurrentTime());

        Role userRole = iRoleRepository.findByRoleName(RoleName.valueOf(registerDto.getRole().toString()));
        user.setRole(userRole);

        iUserRepository.save(user);

        String subject = "Tạo tài khoản thành công";
        String template = "templates/notify_create_account.html";

        Map<String, String> parameters = new HashMap<>();
        parameters.put("email", registerDto.getEmail());
        parameters.put("pass", randomPassword);

        try {
            mailService.sendHtmlEmail(fromMail, registerDto.getEmail(), subject, template, parameters);
            response.put("message", "Account created successfully");
            response.put("status", "success");
        } catch (Exception e) {
            response.put("message", "Failed to send email: " + e.getMessage());
            response.put("status", "fail");
        }

        return response;
    }

    private String generateRandomPassword(int length) {
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 to include all character types.");
        }
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*()";
        String all = upper + lower + digits + special;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        for (int i = 4; i < length; i++) {
            password.append(all.charAt(random.nextInt(all.length())));
        }
        List<Character> pwdChars = password.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        StringBuilder finalPassword = new StringBuilder();
        pwdChars.forEach(finalPassword::append);

        return finalPassword.toString();
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        Map<String, String> response = new HashMap<>();
        if (!jwtUtilities.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtilities.extractUsername(refreshToken);
        String refreshKey = jwtUtilities.extractClaim(refreshToken, claims -> claims.get("key", String.class));

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (jwtUtilities.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        if (!iUserTokenRepository.existsByUser_IdAndRefreshKey(user.getId(), refreshKey)) {
            response.put("error", "Token not found");
            return response;
        }
        String accessKey = UUID.randomUUID().toString();

        LocalDateTime accessTokenExpiration = (getCurrentTime().plus(jwtExpiration, ChronoUnit.MILLIS));

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setAccessKey(accessKey);
        userToken.setRefreshKey(refreshKey);
        userToken.setExpiresAt(accessTokenExpiration);
        userToken.setCreatedAt(getCurrentTime());
        iUserTokenRepository.save(userToken);

        Map<String, String> accessParams = new HashMap<>();
        accessParams.put("email", user.getUsername());
        accessParams.put("key", accessKey);

        String accessToken = jwtUtilities.generateToken(accessParams, "ACCESS_CONTEXT", jwtExpiration);

        response.put("accessToken", accessToken);
        response.put("expiredDate", accessTokenExpiration.toString());

        return response;
    }

    public Map<String, String> updateUserInfo(UpdateUserDto updateUserDto) {
        Map<String, String> response = new HashMap<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new UsernameNotFoundException("User not found or not authenticated");
        }

        String email = authentication.getName();
        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        boolean isUpdated = false;

        if (updateUserDto.getFullName() != null && !updateUserDto.getFullName().equals(user.getFullName())) {
            user.setFullName(updateUserDto.getFullName());
            isUpdated = true;
        }
        if (updateUserDto.getAddress() != null && !updateUserDto.getAddress().equals(user.getAddress())) {
            user.setAddress(updateUserDto.getAddress());
            isUpdated = true;
        }
        if (updateUserDto.getDateOfBirth() != null && !updateUserDto.getDateOfBirth().equals(user.getDateOfBirth())) {
            user.setDateOfBirth((Date) updateUserDto.getDateOfBirth());
            isUpdated = true;
        }
        if (updateUserDto.getIdentityNumber() != null && !updateUserDto.getIdentityNumber().equals(user.getIdentityNumber())) {
            user.setIdentityNumber(updateUserDto.getIdentityNumber());
            isUpdated = true;
        }
        if (updateUserDto.getPhoneNumber() != null && !updateUserDto.getPhoneNumber().equals(user.getPhone())) {
            user.setPhone(updateUserDto.getPhoneNumber());
            isUpdated = true;
        }

        if (isUpdated) {
            iUserRepository.save(user);
            response.put("success", "Update completed!");
        } else {
            response.put("info", "No changes detected.");
        }

        return response;
    }

    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        String email = authentication.getName();
        return iUserRepository.findByEmail(email)
                .map(UserDto::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

