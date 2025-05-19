package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.AssetStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class AssetOfBuildingDto {
    private Long id;
    private String type;
    private AssetStatus status;
    private Double price;
    private Long building_id;
    private Date installationDate;
    private Date lastCheckedDate;
}
