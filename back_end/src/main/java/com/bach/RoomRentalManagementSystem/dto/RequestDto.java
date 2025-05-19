package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.RequestStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class RequestDto {
    private Long id;
    private String room_number;
    private Long building_id;
    private String description;
    private Date startDate;
    private Date endDate;
    private RequestStatus status;
    private String note;
}
