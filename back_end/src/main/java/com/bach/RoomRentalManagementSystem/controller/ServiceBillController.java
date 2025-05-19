package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.MaintenanceExpensesDto;
import com.bach.RoomRentalManagementSystem.dto.ServiceBillDto;
import com.bach.RoomRentalManagementSystem.model.ApprovalStatuses;
import com.bach.RoomRentalManagementSystem.model.BillStatus;
import com.bach.RoomRentalManagementSystem.model.ExpenseType;
import com.bach.RoomRentalManagementSystem.model.ServiceBillName;
import com.bach.RoomRentalManagementSystem.service.ServiceBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
public class ServiceBillController {

    @Autowired
    private ServiceBillService serviceBillService;

    @PostMapping("/admin/service-bill")
    public ResponseEntity<?> createBill(@RequestBody ServiceBillDto serviceBillDto) {
        try {
            Map<String, String> response = serviceBillService.createServiceBill(serviceBillDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/service-bill")
    public ResponseEntity<?> updateBill(@RequestBody ServiceBillDto serviceBillDto) {
        try {
            Map<String, String> response = serviceBillService.updateServiceBill(serviceBillDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/auth/service-bill/{bill_id}")
    public ResponseEntity<?> getBillById(@PathVariable Long bill_id) {
        try {
            ServiceBillDto serviceBillDto = serviceBillService.getServiceBillById(bill_id);
            return ResponseEntity.ok(serviceBillDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/bill-status")
    public ResponseEntity<?> getBillStatus() {
        try {
            List<BillStatus> billStatusList = serviceBillService.getAllBillStatus();
            return ResponseEntity.ok(billStatusList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/service-name")
    public ResponseEntity<?> getServiceName() {
        try {
            List<ServiceBillName> serviceBillNames = serviceBillService.getAllServicesBillName();
            return ResponseEntity.ok(serviceBillNames);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/service-bill")
    public ResponseEntity<?> deleteBill(@RequestBody Long id) {
        try {
            Map<String, String> response = serviceBillService.deleteServiceBill(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/service-bill")
    public ResponseEntity<?> getBillFilter(
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<ServiceBillDto> serviceBillDtos = serviceBillService.getServiceBillFilter(
                    room_id, building_id, status, type, startDate, endDate,
                    page, size);

            return ResponseEntity.ok(serviceBillDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
}