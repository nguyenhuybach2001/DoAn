package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.VehicleType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleDto {

    private Long id;
    private String licensePlate;
    private VehicleType type;
    private String roomNumber;
    private Long buildingId;
}
