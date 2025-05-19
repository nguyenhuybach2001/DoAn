package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.ContractStatus;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class RentalContractDto {
    private Long id;
    private Date startDate;
    private Date endDate;
    private Double rentPrice;
    private ContractStatus status;
    private String room_number;
    private Long buildingId;
    private String pdfFile;
    private Long customerId;
}
