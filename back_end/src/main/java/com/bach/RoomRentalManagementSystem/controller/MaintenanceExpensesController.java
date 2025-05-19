package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.ExpensesImportDto;
import com.bach.RoomRentalManagementSystem.dto.MaintenanceExpensesDto;
import com.bach.RoomRentalManagementSystem.model.ApprovalStatuses;
import com.bach.RoomRentalManagementSystem.model.BillStatus;
import com.bach.RoomRentalManagementSystem.model.ExpenseType;
import com.bach.RoomRentalManagementSystem.service.MaintenanceExpensesService;
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
public class MaintenanceExpensesController {
    
    @Autowired
    private MaintenanceExpensesService maintenanceExpensesService;

    @PostMapping("/staff/expenses")
    public ResponseEntity<?> createExpenses(@RequestBody ExpensesImportDto expensesImportDto) {
        try {
            Map<String, String> response = maintenanceExpensesService.createExpense(expensesImportDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/expenses")
    public ResponseEntity<?> updateExpenses(@RequestBody ExpensesImportDto expensesImportDto) {
        try {
            Map<String, String> response = maintenanceExpensesService.updateExpense(expensesImportDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/expenses/status/{id}")
    public ResponseEntity<Map<String, String>> updateExpenseStatus(
            @PathVariable Long id,
            @RequestBody ApprovalStatuses approvalStatuses) {
        try {
            Map<String, String> response = maintenanceExpensesService.setApprovalStatuses(id, approvalStatuses);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage(), "status", "error"));
        }
    }

    @GetMapping("/admin/expenses/{expenses_id}")
    public ResponseEntity<?> getExpenseById(@PathVariable Long expenses_id) {
        try {
            MaintenanceExpensesDto expensesDto = maintenanceExpensesService.getExpenseById(expenses_id);
            return ResponseEntity.ok(Map.of("expenses", expensesDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/expenses-type")
    public ResponseEntity<?> getExpenseType() {
        try {
            List<ExpenseType> expenseTypeList = maintenanceExpensesService.getExpenseType();
            return ResponseEntity.ok(Map.of("expense type", expenseTypeList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/approval-status")
    public ResponseEntity<?> getApprovalStatuses() {
        try {
            List<ApprovalStatuses> approvalStatusesList = maintenanceExpensesService.getApprovalStatuses();
            return ResponseEntity.ok(Map.of("approval status", approvalStatusesList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/staff/expense/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            Map<String, String> response = maintenanceExpensesService.deleteExpense(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/expenses")
    public ResponseEntity<?> getExpenseFilter(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String approvalStatuses,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<MaintenanceExpensesDto> maintenanceExpensesDtoPage = maintenanceExpensesService.getExpensesFilter(
                    type, startDate, endDate,
                    minAmount, maxAmount, status, approvalStatuses , page, size);

            return ResponseEntity.ok(maintenanceExpensesDtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
}
