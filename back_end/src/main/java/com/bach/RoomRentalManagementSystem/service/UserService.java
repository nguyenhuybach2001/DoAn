package com.bach.RoomRentalManagementSystem.service;

import java.time.temporal.ChronoUnit;
import java.util.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;

import com.bach.RoomRentalManagementSystem.dto.ActiveDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.security.CustomerUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements ImplUserService {
    private LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

    @Value("${fe.host}")
    private String feHost;

    @Value("${maijet.from}")
    private String fromMail;

    @Autowired
    private HttpServletRequest request;

    private final AuthenticationManager authenticationManager;
    private final IUserRepository iUserRepository;
    private final IRoleRepository iRoleRepository;
    private final IUserTokenRepository iUserTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilities jwtUtilities;
    private final MailService mailService;
    private final CustomerUserDetailsService customerUserDetailsService;


    @Override
    public Role saveRole(Role role) {
        return iRoleRepository.save(role);
    }

    @Override
    public User saverUser(User user) {
        return iUserRepository.save(user);
    }

    // service đăng ký cho khách
    @Override
    public Map<String, String> register(RegisterDto registerDto) {
        Map<String, String> response = new HashMap<>();

        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
            response.put("message", "Email is already taken!");
            response.put("status", "fail");
        } else {

            User user = new User();
            user.setEmail(registerDto.getEmail());
            user.setFullName(registerDto.getFullName());
            user.setPasswordHash(passwordEncoder.encode(registerDto.getPassword()));
            user.setRole(iRoleRepository.findByRoleName(RoleName.valueOf("CUSTOMER")));
            LocalDateTime createdAt = getCurrentTime();
            user.setCreatedAt(createdAt);

            iUserRepository.save(user);

            Map<String, String> registerParams = new HashMap<>();
            registerParams.put("email", registerDto.getEmail());
            registerParams.put("createdAt", createdAt.toString());

            String registerToken = jwtUtilities.generateToken(registerParams, "REGISTER_CONTEXT");
            String toMail = registerDto.getEmail();
            String subject = "Xác nhận tài khoản";
            String template = "templates/validate.html";
            String link = String.format("%s?token=%s", feHost, registerToken);
            Map<String, String> parameters = new HashMap<>();
            parameters.put("name", registerDto.getFullName());
            parameters.put("link", link);

            try {
                mailService.sendHtmlEmail(fromMail, toMail, subject, template, parameters);
                response.put("message", "Registration successful.");
                response.put("status", "success");
            } catch (Exception e) {
                response.put("message", "Failed to send verification email: " + e.getMessage());
                response.put("status", "fail");
            }
        }
        return response;
    }

    public Map<String, String> activeAcc(ActiveDto activeDto) {
        Map<String, String> response = new HashMap<>();

        // Kiểm tra token hợp lệ
        if (!jwtUtilities.validateToken(activeDto.getToken())) {
            response.put("message", "Token is invalid!");
            response.put("status", "fail");
            return response;
        }

        // Giải mã token để lấy thông tin email
        String emailFromToken = jwtUtilities.extractUsername(activeDto.getToken());

        if (!emailFromToken.equals(activeDto.getEmail())) {
            response.put("message", "Email does not match with token!");
            response.put("status", "fail");
            return response;
        }

        // Kiểm tra user tồn tại
        User user = iUserRepository.findByEmail(activeDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Kiểm tra nếu tài khoản đã kích hoạt rồi
        if (user.getIsActive()) {
            response.put("message", "Account is already active!");
            response.put("status", "fail");
            return response;
        }

        // Kích hoạt tài khoản
        user.setIsActive(true);
        iUserRepository.save(user);

        response.put("message", "Account activated successfully.");
        response.put("status", "success");
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

        String accessKey = generateUniqueString(50);
        String refreshKey = generateUniqueString(100);

        Map<String, String> accessParams = new HashMap<>();
        accessParams.put("email", user.getUsername());
        accessParams.put("role", user.getRole().toString());
        accessParams.put("key", accessKey);

        String accessToken = jwtUtilities.generateToken(accessParams, "ACCESS_CONTEXT");

        Map<String, String> refreshParams = new HashMap<>();
        refreshParams.put("email", user.getUsername());
        refreshParams.put("role", user.getRole().toString());
        refreshParams.put("key", refreshKey);
        refreshParams.put("accessToken", accessToken);

        String refreshToken = jwtUtilities.generateToken(refreshParams, "REFRESH_CONTEXT");

        LocalDateTime accessTokenExpiration = (getCurrentTime().plus(jwtUtilities.getJwtExpiration(), ChronoUnit.MILLIS));

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

    // Admin tạo account staff
    public Map<String, String> createAccount(RegisterDto registerDto) {
        Map<String, String> response = new HashMap<>();

        if (iUserRepository.existsByEmail(registerDto.getEmail())) {
            response.put("message", "Email is already registered!");
            response.put("status", "fail");
            return response;
        }
        String randomPassword = generateRandomPassword(8);

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setFullName(registerDto.getFullName());
        user.setPasswordHash(passwordEncoder.encode(randomPassword));
        user.setIsActive(true);
        user.setCreatedAt(getCurrentTime());

        Role userRole = iRoleRepository.findByRoleName(RoleName.valueOf("STAFF"));
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


    public static String generateUniqueString(int length) {
        String uniqueString = UUID.randomUUID().toString().replace("-", "");
        return uniqueString.length() > length ? uniqueString.substring(0, length) : uniqueString;
    }

    public Map<String, String> refreshAccessToken(String refreshToken) {
        if (!jwtUtilities.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String email = jwtUtilities.extractUsername(refreshToken);
        String refreshKey = jwtUtilities.extractClaim(refreshToken, claims -> claims.get("key", String.class));

        User user = iUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Map<String, String> response = new HashMap<>();

        if (jwtUtilities.isTokenExpired(refreshToken)) {
            throw new RuntimeException("Refresh token has expired");
        }

        if (iUserTokenRepository.existsByUser_IdAndRefreshKey(refreshKey, user.getId())) {
            response.put("message", "Token not found");
            response.put("status", "fail");
        }
        String accessKey = generateUniqueString(50);

        LocalDateTime accessTokenExpiration = (getCurrentTime().plus(jwtUtilities.getJwtExpiration(), ChronoUnit.MILLIS));

        UserToken userToken = new UserToken();
        userToken.setUser(user);
        userToken.setAccessKey(accessKey);
        userToken.setRefreshKey(refreshKey);
        userToken.setExpiresAt(accessTokenExpiration);
        userToken.setCreatedAt(getCurrentTime());
        iUserTokenRepository.save(userToken);

        Map<String, String> accessParams = new HashMap<>();
        accessParams.put("email", user.getUsername());
        accessParams.put("role", user.getRole().toString());
        accessParams.put("key", accessKey);

        String accessToken = jwtUtilities.generateToken(accessParams, "ACCESS_CONTEXT");

        response.put("accessToken", accessToken);
        response.put("expiredDate", accessTokenExpiration.toString());

        return response;
    }

    @Override
    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return iUserRepository.findByEmail(email).map(UserDto::new).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}

