package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.VehicleDto;
import com.bach.RoomRentalManagementSystem.model.VehicleType;
import com.bach.RoomRentalManagementSystem.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/admin/vehicles")
    public ResponseEntity<?> createVehicles(@RequestBody List<VehicleDto> vehicleDtos) {
        try {
            Map<String, String> response = vehicleService.createVehicles(vehicleDtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/vehicle")
    public ResponseEntity<?> updateVehicle(@RequestBody VehicleDto vehicleDto) {
        try {
            Map<String, String> response = vehicleService.updateVehicle(vehicleDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/vehicle")
    public ResponseEntity<?> deleteVehicles(@RequestBody List<Long> ids) {
        try {
            Map<String, String> response = vehicleService.deleteVehicles(ids);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping ("/admin/vehicle-type")
    public ResponseEntity<?> getVehicleType() {
        try {
            List<VehicleType> vehicleTypeList = vehicleService.getAllVehicleType();
            return ResponseEntity.ok(vehicleTypeList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/admin/vehicles")
    public ResponseEntity<?> getAllVehicles(
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<VehicleDto> dtoPage = vehicleService.getVehiclesFilter(
                    roomNumber,
                    buildingId,
                    licensePlate,
                    type,
                    page,
                    size
            );

            return ResponseEntity.ok(dtoPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
