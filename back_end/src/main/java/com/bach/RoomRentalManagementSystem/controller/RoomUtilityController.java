package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.RoomUtilityDto;
import com.bach.RoomRentalManagementSystem.service.RoomUtilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/auth/room-utility","/staff/room-utility"})
public class RoomUtilityController {

    @Autowired
    private RoomUtilityService roomUtilityService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createRoomUtility(@RequestBody List<RoomUtilityDto> roomUtilityDtoList) {
        try {
            Map<String, String> response = roomUtilityService.createRoomUtilityService(roomUtilityDtoList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping
    public ResponseEntity<Map<String, String>> updateRoomUtility(@RequestBody RoomUtilityDto roomUtilityDto) {
        try {
            Map<String, String> response = roomUtilityService.updateRoomUtilityService(roomUtilityDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomUtilityDto> getRoomUtilityById(@PathVariable Long id) {
        try {
            RoomUtilityDto roomUtilityDto = roomUtilityService.getRoomUtilityById(id);
            return ResponseEntity.ok(roomUtilityDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<RoomUtilityDto>> getRoomUtilityFilter(
            @RequestParam(required = false) String room_number,
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) Double minUsage,
            @RequestParam(required = false) Double maxUsage,
            @RequestParam(required = false) String utilityUsage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<RoomUtilityDto> roomUtilityPage = roomUtilityService.getRoomUtilityFilter(
                room_number, building_id, startDate, endDate, minUsage, maxUsage, utilityUsage, page, size);

        return ResponseEntity.ok(roomUtilityPage);
    }
}
