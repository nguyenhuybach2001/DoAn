package com.bach.RoomRentalManagementSystem.model;

import com.bach.RoomRentalManagementSystem.config.AtLeastOneAssetPresent;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AtLeastOneAssetPresent
@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "maintenance_expenses")
public class MaintenanceExpenses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expenses_id")
    private Long expensesId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_in_room_id", referencedColumnName = "id", nullable = true)
    private AssetInRoom assetInRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_of_building_id", referencedColumnName = "id", nullable = true)
    private AssetOfBuilding assetOfBuilding;

    @Enumerated(EnumType.STRING)
    @Column(name = "expenseType")
    private ExpenseType expenseType;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "date")
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "bill_status")
    private BillStatus billStatus = BillStatus.UNPAID;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_statuses")
    private ApprovalStatuses approvalStatuses = ApprovalStatuses.WAITING;

    @Column(name = "image")
    private String image;

    @Column(name = "note")
    private String note;
}
