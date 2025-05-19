package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.BillStatus;
import com.bach.RoomRentalManagementSystem.model.ServiceBillName;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class ServiceBillDto {

    private Long billId;
    private String room_number;
    private Long building_id;
    private ServiceBillName service;
    private Double amount;
    private Date date;
    private BillStatus status;
    private String note;

}
