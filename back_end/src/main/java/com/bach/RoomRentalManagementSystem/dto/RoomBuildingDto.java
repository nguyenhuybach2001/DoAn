package com.bach.RoomRentalManagementSystem.dto;

import lombok.Data;

@Data
public class RoomBuildingDto {
    String roomNumber;
    String buildingName;

    public RoomBuildingDto(String roomNumber, String buildingName) {
        this.roomNumber = roomNumber;
        this.buildingName = buildingName;
    }
}
