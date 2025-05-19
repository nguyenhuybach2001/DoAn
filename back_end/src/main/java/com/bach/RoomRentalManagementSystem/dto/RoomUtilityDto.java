package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.RoomUtilityType;
import lombok.Builder;
import lombok.Data;
import java.sql.Date;

@Data
@Builder
public class RoomUtilityDto {

    private Long id;
    private Date date;
    private Double usage;
    private RoomUtilityType utilityType;
    private String room_number;
    private Long building_id;
}
