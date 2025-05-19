package com.bach.RoomRentalManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDto {
    @NotBlank
    private String fullName;
    private Date dateOfBirth;
    private String identityNumber;
    private String address;
    private String phoneNumber;
}
