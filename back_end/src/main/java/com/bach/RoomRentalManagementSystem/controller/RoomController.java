package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.RoomDto;
import com.bach.RoomRentalManagementSystem.dto.RoomsDto;
import com.bach.RoomRentalManagementSystem.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/admin/room")
    public ResponseEntity<?> createRoom(@Valid @RequestBody RoomDto roomDto) {
        try {
            RoomDto createdRoom = roomService.createRoom(roomDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Room created successfully"));
        } catch (IllegalStateException e) {
            // Trùng room number
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (NoSuchElementException e) {
            // Building không tồn tại
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Ví dụ RoomStatus không hợp lệ
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            // Các lỗi không mong muốn khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred"));
        }
    }


    @PostMapping("/admin/rooms")
    public ResponseEntity<Map<String, String>> createRooms(@Valid @RequestBody RoomsDto roomsDto) {
        try {
            Map<String, String> response = roomService.createRooms(roomsDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred while creating the room"));
        }
    }

    // API cập nhật thông tin phòng
    @PutMapping("/admin/room")
    public ResponseEntity<Map<String, String>> updateRoom(@Valid @RequestBody RoomDto roomDto) {
        try {
            RoomDto updatedRoom = roomService.updateRoom(roomDto);
            return ResponseEntity.ok(Map.of("message", "Room updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "An error occurred while updating the room"));
        }
    }

    // API xóa phòng
    @DeleteMapping("/admin/rooms")
    public ResponseEntity<Map<String, String>> deleteRoom(@RequestBody List<Long> roomIds) {
        try {
            roomService.deleteRooms(roomIds);
            return ResponseEntity.ok(Map.of("message", "Rooms deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "An error occurred while deleting the room"));
        }
    }

    @PutMapping("/admin/rooms")
    public ResponseEntity<Map<String, String>> showRoom(@RequestBody List<Long> roomIds) {
        try {
            roomService.showRooms(roomIds);
            return ResponseEntity.ok(Map.of("message", "Rooms show successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "An error occurred while deleting the room"));
        }
    }

    @PutMapping("/admin/room/manager")
    public ResponseEntity<?> setRoomManager(@RequestParam Long room_id, @RequestParam String email) {
        try {
            Map<String, String> response = roomService.setRoomManager(room_id, email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // API lấy thông tin phòng theo ID
    @GetMapping("/admin/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoomById(@PathVariable Long roomId) {
        try {
            RoomDto room = roomService.getRoomById(roomId);
            return ResponseEntity.ok(Map.of("room", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Room not found"));
        }
    }

    // API lấy tất cả phòng
    @GetMapping("/auth/rooms")
    public ResponseEntity<?> getAllRooms(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer maxOccupants,
            @RequestParam(required = false) Long minAcreage,
            @RequestParam(required = false) Long maxAcreage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<RoomDto> rooms = roomService.getAllRooms(
                    buildingId,
                    status,
                    minPrice,
                    maxPrice,
                    floor,
                    roomType,
                    maxOccupants,
                    minAcreage,
                    maxAcreage,
                    page,
                    size
            );
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<?> getAllRoomsPublic(
            @RequestParam(required = false) Long buildingId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String roomType,
            @RequestParam(required = false) Integer maxOccupants,
            @RequestParam(required = false) Long minAcreage,
            @RequestParam(required = false) Long maxAcreage,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Page<RoomDto> rooms = roomService.getAllRoomsPublic(
                    buildingId,
                    status,
                    minPrice,
                    maxPrice,
                    floor,
                    roomType,
                    maxOccupants,
                    minAcreage,
                    maxAcreage,
                    page,
                    size
            );
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
