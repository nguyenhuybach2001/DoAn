package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.AssetInRoomDto;
import com.bach.RoomRentalManagementSystem.model.AssetStatus;
import com.bach.RoomRentalManagementSystem.service.AssetInRoomService;
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
public class AssetInRoomController {
    @Autowired
    private AssetInRoomService assetInRoomService;

    @PostMapping("/admin/asset-in-room")
    public ResponseEntity<?> createAsset(@RequestBody List<AssetInRoomDto> assetsInRoomDto) {
        try {
            Map<String, String> response =assetInRoomService.createAssetsInRoomService(assetsInRoomDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/admin/asset-in-room")
    public ResponseEntity<?> updateAsset(@RequestBody AssetInRoomDto assetInRoomDto) {
        try {
            Map<String, String> response =assetInRoomService.updateAssetInRoomService(assetInRoomDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/asset-in-room/{asset_id}")
    public ResponseEntity<?> getAssetById(@PathVariable Long asset_id) {
        try {
            AssetInRoomDto asset = assetInRoomService.getAssetById(asset_id);
            return ResponseEntity.ok(Map.of("asset", asset));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/admin/asset-in-room")
    public ResponseEntity<?> updateAsset(@RequestBody List<Long> ids) {
        try {
            Map<String, String> response = assetInRoomService.deleteAssetsInRoomService(ids);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/asset-status")
    public ResponseEntity<?> getAssetStatus() {
        try {
            List<AssetStatus> response = assetInRoomService.getAllAssetStatus();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(List.of(new AssetStatus[]{ AssetStatus.valueOf("ERROR") }));
        }
    }

    @GetMapping("/admin/assets-in-room")
    public ResponseEntity<?> getAssetInRoomFilter(
            @RequestParam(required = false) String room_number,
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
            Page<AssetInRoomDto> assetInRoomPage = assetInRoomService.getAssetInRoomFilter(
                    room_number, building_id, type, status, minPrice, maxPrice,
                    startInstall, endInstall, startCheck, endCheck, page, size);

            return ResponseEntity.ok(assetInRoomPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
}
