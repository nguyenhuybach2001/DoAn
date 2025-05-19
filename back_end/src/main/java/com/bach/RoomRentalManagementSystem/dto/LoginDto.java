package com.bach.RoomRentalManagementSystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Email cannot blank!")
    @Email(message = "Email is invalid!")
    private String email ;
    private String password ;
}
