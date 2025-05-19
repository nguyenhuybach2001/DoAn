package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.AssetStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class AssetInRoomDto {
    private Long id;
    private String type;
    private AssetStatus status;
    private String room_number;
    private Long building_id;
    private Double price;
    private Date installationDate;
    private Date lastCheckedDate;
}
