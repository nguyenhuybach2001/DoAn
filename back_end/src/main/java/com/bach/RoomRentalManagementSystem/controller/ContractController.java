package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.AssetOfBuildingDto;
import com.bach.RoomRentalManagementSystem.dto.RentalContractDto;
import com.bach.RoomRentalManagementSystem.model.ContractStatus;
import com.bach.RoomRentalManagementSystem.service.ContractService;
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
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/admin/contract")
    public ResponseEntity<?> createContract(@RequestBody RentalContractDto rentalContractDto) {
        try {
            Map<String, String> response = contractService.createRentalContractService(rentalContractDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/contract")
    public ResponseEntity<?> updateContract(@RequestBody RentalContractDto rentalContractDto) {
        try {
            Map<String, String> response = contractService.updateRentalContractService(rentalContractDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/contract/{contract_id}")
    public ResponseEntity<?> getContractById(@PathVariable Long contract_id) {
        try {
            RentalContractDto contract = contractService.getContractById(contract_id);
            return ResponseEntity.ok(Map.of("contract", contract));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/contract-status")
    public ResponseEntity<?> getContractStatus() {
        try {
            List<ContractStatus> contractStatuses = contractService.getAllContractStatus();
            return ResponseEntity.ok(Map.of("status", contractStatuses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/contract")
    public ResponseEntity<?> deleteContract(@RequestBody Long id) {
        try {
            Map<String, String> response = contractService.deleteRentalContract(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/auth/contract")
    public ResponseEntity<?> getContractFilter(
            @RequestParam(required = false) String room_number,
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<RentalContractDto> rentalContractDtoPage = contractService.getRentalContractFilter(
                    room_number, building_id, status, startDate, endDate,
                    minPrice, maxPrice, page, size);

            return ResponseEntity.ok(rentalContractDtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
}
