package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.BuildingDto;
import com.bach.RoomRentalManagementSystem.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Validated
public class BuildingController {

    @Autowired
    private BuildingService buildingService;

    @PostMapping("/admin/building")
    public ResponseEntity<?> createBuilding(@RequestBody BuildingDto buildingDto) {
        try {
            Map<String, String> response = buildingService.createBuildingService(buildingDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/building")
    public ResponseEntity<?> getBuildingsByAdmin() {
        try {
            List<BuildingDto> response = buildingService.getListOfBuildingByAdmin();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

//    @GetMapping("/staff/building")
//    public ResponseEntity<?> getBuildings(@RequestParam Long staff_id) {
//        try {
//            List<BuildingDto> response = buildingService.getListOfBuildingByStaff(staff_id);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }

    @PutMapping("/admin/building")
    public ResponseEntity<?> updateBuildings(@RequestBody BuildingDto buildingDto) {
        System.out.println("Received DTO: " + buildingDto);
        try {
            Map<String, String> response = buildingService.updateBuildingService(buildingDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

//    @PutMapping("/admin/building/manager")
//    public ResponseEntity<?> setBuildingManager(@RequestParam Long building_id, String email) {
//        try {
//            Map<String, String> response = buildingService.setBuildingManager(building_id, email);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }

    @DeleteMapping("/admin/building")
    public ResponseEntity<?> deleteBuildings(@RequestParam Long building_id) {
        try {
            Map<String, String> response = buildingService.deleteBuildingService(building_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/building/active")
    public ResponseEntity<?> showBuilding(@RequestParam Long building_id) {
        try {
            Map<String, String> response = buildingService.showBuildingService(building_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
