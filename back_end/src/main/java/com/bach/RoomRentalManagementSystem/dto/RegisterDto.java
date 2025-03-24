package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.RoleName;

import lombok.Data;

@Data
public class RegisterDto{

    String fullName ;
    String email;
    String password ;
    RoleName roleName;
}
