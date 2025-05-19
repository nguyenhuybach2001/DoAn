package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.MaintenanceExpensesDto;
import com.bach.RoomRentalManagementSystem.dto.RequestDto;
import com.bach.RoomRentalManagementSystem.model.ApprovalStatuses;
import com.bach.RoomRentalManagementSystem.model.BillStatus;
import com.bach.RoomRentalManagementSystem.model.ExpenseType;
import com.bach.RoomRentalManagementSystem.model.RequestStatus;
import com.bach.RoomRentalManagementSystem.service.RequestService;
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
public class RequestController {

    @Autowired
    private RequestService requestService;

    @PostMapping("/customer/request")
    public ResponseEntity<?> createRequest(@RequestBody RequestDto requestDto) {
        try {
            Map<String, String> response = requestService.createRequestService(requestDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/customer/request")
    public ResponseEntity<?> updateExpenses(@RequestBody RequestDto requestDto) {
        try {
            Map<String, String> response = requestService.updateRequestService(requestDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/request/{request_id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long request_id) {
        try {
            RequestDto requestDto = requestService.getRequestById(request_id);
            return ResponseEntity.ok(Map.of("request", requestDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/staff/request-status/{id}")
    public ResponseEntity<Map<String, String>> updateRequestStatus(
            @PathVariable Long id,
            @RequestBody RequestStatus status) {

        try {
            Map<String, String> response = requestService.setRequestStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body(Map.of("message", e.getMessage(), "status", "error"));
        }
    }

    @GetMapping("/admin/request-status")
    public ResponseEntity<?> getRequestStatus() {
        try {
            List<RequestStatus> requestStatusList = requestService.getAllRequestStatus();
            return ResponseEntity.ok(Map.of("request status", requestStatusList));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/request")
    public ResponseEntity<?> deleteRequest(@RequestBody Long id) {
        try {
            Map<String, String> response = requestService.deleteRequest(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/request")
    public ResponseEntity<?> getRequestFilter(
            @RequestParam(required = false) String room_number,
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<RequestDto> requestDtos = requestService.getRequestFilter(
                    room_number, building_id, RequestStatus.valueOf(status), startDate, endDate, page, size);

            return ResponseEntity.ok(requestDtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
}
