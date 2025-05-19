package com.bach.RoomRentalManagementSystem.dto;

import lombok.Builder;
import lombok.Data;


import java.util.List;

@Data
@Builder
public class BuildingDto {

    private Long id;
    private String name;
    private String address;
    private Integer totalRoom;
    private String image;
    private Boolean isActive;
}
