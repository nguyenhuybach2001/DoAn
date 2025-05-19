package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.RentalContract;
import com.bach.RoomRentalManagementSystem.model.RoleName;
import com.bach.RoomRentalManagementSystem.model.Room;
import com.bach.RoomRentalManagementSystem.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private String fullName;
    private String email;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    private String identityNumber;
    private String address;
    private String phoneNumber;
    private Boolean isPasswordChanged;
    private RoleName role;
    private Long id;
    private List<Long> contracts_id;

    public UserDto(User user) {
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.dateOfBirth = user.getDateOfBirth();
        this.identityNumber = user.getIdentityNumber();
        this.address = user.getAddress();
        this.phoneNumber = user.getPhone();
        this.isPasswordChanged = user.getIsPasswordChanged();
        this.role = RoleName.valueOf(user.getRole().getRoleName());
        this.id = user.getId();
        this.contracts_id = user.getContracts().stream()
                .map(RentalContract::getContractId)
                .collect(Collectors.toList());
    }
}
