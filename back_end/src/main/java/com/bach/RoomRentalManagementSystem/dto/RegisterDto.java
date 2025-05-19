package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.RoleName;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterDto {

    @NotBlank(message = "Full name cannot blank!")
    String fullName;
    @NotBlank(message = "Email cannot blank!")
    @Email(message = "Email is invalid!")
    String email;
//    @NotEmpty(message = "Password cannot be empty")
//    @Pattern(
//            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
//            message = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character."
//    )
//    String password;
    @NotNull(message = "Role cannot be null")
    RoleName role;
}
