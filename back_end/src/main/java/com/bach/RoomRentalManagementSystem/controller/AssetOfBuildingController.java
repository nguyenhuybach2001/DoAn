package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.AssetInRoomDto;
import com.bach.RoomRentalManagementSystem.dto.AssetOfBuildingDto;
import com.bach.RoomRentalManagementSystem.service.AssetOfBuildingService;
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
public class AssetOfBuildingController {

    @Autowired
    private AssetOfBuildingService assetOfBuildingService;

    @PostMapping("/admin/asset-of-building")
    public ResponseEntity<?> createAsset(@RequestBody List<AssetOfBuildingDto> assetOfBuildingDtos) {
        try {
            Map<String, String> response = assetOfBuildingService.createAssetsOfBuildingService(assetOfBuildingDtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/asset-of-building")
    public ResponseEntity<?> updateAsset(@RequestBody AssetOfBuildingDto assetOfBuildingDto) {
        try {
            Map<String, String> response = assetOfBuildingService.updateAssetOfBuildingService(assetOfBuildingDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/asset-of-building/{asset_id}")
    public ResponseEntity<?> getAssetById(@PathVariable Long asset_id) {
        try {
            AssetOfBuildingDto asset = assetOfBuildingService.getAssetById(asset_id);
            return ResponseEntity.ok(Map.of("asset", asset));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/asset-of-building")
    public ResponseEntity<?> updateAsset(@RequestBody List<Long> ids) {
        try {
            Map<String, String> response = assetOfBuildingService.deleteAssetsOfBuilding(ids);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/assets-of-building")
    public ResponseEntity<?> getAssetOfBuildingFilter(
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startInstall,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endInstall,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startCheck,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endCheck,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Page<AssetOfBuildingDto> assetOfBuildingDtoPagePage = assetOfBuildingService.getAssetOfBuildingFilter(
                    building_id, type, status, minPrice, maxPrice,
                    startInstall, endInstall, startCheck, endCheck, page, size);

            return ResponseEntity.ok(assetOfBuildingDtoPagePage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

}
