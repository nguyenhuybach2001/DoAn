package com.bach.RoomRentalManagementSystem.dto;

import com.bach.RoomRentalManagementSystem.model.ApprovalStatuses;
import com.bach.RoomRentalManagementSystem.model.BillStatus;
import com.bach.RoomRentalManagementSystem.model.ExpenseType;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data
@Builder
public class ExpensesImportDto {

    private Long id;
    private Long asset_in_room_id;
    private Long asset_of_building_id;
    private ExpenseType expense_type;
    private String description;
    private Double amount;
    private Date date;
    private BillStatus status;
    private ApprovalStatuses approval;
    private String image;
    private String note;
}
