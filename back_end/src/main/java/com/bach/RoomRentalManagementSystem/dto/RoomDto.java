package com.bach.RoomRentalManagementSystem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoomDto {

    private Long roomId;          // ID phòng trọ
    private String roomNumber;    // Số phòng
    private Long acreage;         // Diện tích phòng
    private Double price;         // Giá phòng
    private String status;        // Trạng thái phòng
    private String roomType;      // Loại phòng
    private Integer floor;
    private Integer maxOccupants; // Số người tối đa có thể ở
    private String image;
    private Long buildingId;
    private String description;   // Mô tả phòng
    private Boolean isActive;
    private String address;
    private UserDto staff;


    private List<RentalContractDto> contracts;
    private RentalContractDto currentContract;
}