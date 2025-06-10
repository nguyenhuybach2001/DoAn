package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.RentalContractDto;
import com.bach.RoomRentalManagementSystem.dto.RoomDto;
import com.bach.RoomRentalManagementSystem.dto.RoomsDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IBuildingRepository;
import com.bach.RoomRentalManagementSystem.repository.IRentalContractRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RoomService {

    private final IRoomRepository roomRepository;
    private final IBuildingRepository buildingRepository;
    private final IUserRepository userRepository;
    private final IRentalContractRepository rentalContractRepository;
    private final UserService userService;

    public RoomDto createRoom(RoomDto roomDto) {
        boolean exists = roomRepository.existsByRoomNumberAndBuilding_BuildingIdAndIsActive(
                roomDto.getRoomNumber(),
                roomDto.getBuildingId(),
                true
        );

        if (exists) {
            throw new IllegalStateException("Room number already exists in this building and is active.");
        }

        Building building = buildingRepository.findByBuildingId(roomDto.getBuildingId());
        if (building == null) {
            throw new NoSuchElementException("Building not found with ID: " + roomDto.getBuildingId());
        }

        Room room = new Room();
        room.setRoomNumber(roomDto.getRoomNumber());
        room.setAcreage(roomDto.getAcreage());
        room.setPrice(roomDto.getPrice());
        room.setStatus(RoomStatus.valueOf(roomDto.getStatus()));
        room.setRoomType(roomDto.getRoomType());
        room.setFloor(roomDto.getFloor());
        room.setMaxOccupants(roomDto.getMaxOccupants());
        room.setImage(roomDto.getImage());
        room.setDescription(roomDto.getDescription());
        room.setBuilding(building);

        Room savedRoom = roomRepository.save(room);
        int totalRooms = roomRepository.countByBuilding_BuildingId(building.getBuildingId());
        building.setTotalRooms(totalRooms);
        buildingRepository.save(building);
        return mapToDto(savedRoom);
    }


    public Map<String, String> createRooms(RoomsDto roomsDto) {
        List<Room> rooms = new ArrayList<>();

        List<String> existingRoomNumbers = roomRepository.findActiveRoomNumbersInBuilding(roomsDto.getBuilding_id());

        Building building = buildingRepository.findByBuildingId(roomsDto.getBuilding_id());

        for (RoomDto roomDto : roomsDto.getRoomsDto()) {
            if (existingRoomNumbers.contains(roomDto.getRoomNumber())) {
                throw new IllegalArgumentException("Room " + roomDto.getRoomNumber() + " already exists in building " + roomsDto.getBuilding_id());
            }

            Room room = new Room();
            room.setRoomNumber(roomDto.getRoomNumber());
            room.setAcreage(roomDto.getAcreage());
            room.setPrice(roomDto.getPrice());
            room.setStatus(RoomStatus.valueOf(roomDto.getStatus()));
            room.setRoomType(roomDto.getRoomType());
            room.setFloor(roomDto.getFloor());
            room.setMaxOccupants(roomDto.getMaxOccupants());
            room.setImage(roomDto.getImage());
            room.setDescription(roomDto.getDescription());
            room.setBuilding(building);
            rooms.add(room);
        }

        roomRepository.saveAll(rooms);

        return Map.of("message", "Rooms created successfully", "status", "success");
    }

    public RoomDto updateRoom(RoomDto roomDto) {

        Room room = roomRepository.findById(roomDto.getRoomId())
                .orElseThrow(() -> new NoSuchElementException("Room not found"));

        room.setRoomNumber(roomDto.getRoomNumber());
        room.setAcreage(roomDto.getAcreage());
        room.setPrice(roomDto.getPrice());
        room.setStatus(RoomStatus.valueOf(roomDto.getStatus()));
        room.setRoomType(roomDto.getRoomType());
        room.setFloor(roomDto.getFloor());
        room.setMaxOccupants(roomDto.getMaxOccupants());
        room.setImage(roomDto.getImage());
        room.setDescription(roomDto.getDescription());

        Room updatedRoom = roomRepository.save(room);
        return mapToDto(updatedRoom);
    }

    public void deleteRooms(List<Long> roomIds) {
        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("No room IDs provided to delete.");
        }
        List<Room> rooms = roomRepository.findAllById(roomIds);

        if (rooms.isEmpty()) {
            throw new NoSuchElementException("No rooms found with the provided IDs.");
        }
        for (Room room : rooms) {
            room.setIsActive(false);
        }
        roomRepository.saveAll(rooms);
    }

    public void showRooms(List<Long> roomIds) {
        if (roomIds.isEmpty()) {
            throw new IllegalArgumentException("No room IDs provided to delete.");
        }
        List<Room> rooms = roomRepository.findAllById(roomIds);

        if (rooms.isEmpty()) {
            throw new NoSuchElementException("No rooms found with the provided IDs.");
        }
        for (Room room : rooms) {
            if (!room.getBuilding().getIsActive()) {
                throw new IllegalStateException("Không thể mở phòng " + room.getRoomNumber()
                        + " vì tòa nhà " + room.getBuilding().getName() + " đang bị ẩn.");
            }
            room.setIsActive(true);
        }
        roomRepository.saveAll(rooms);
    }

    public Map<String, String> setRoomManager(Long room_id, String email) {
        Map<String, String> response = new HashMap<>();
        User staff = userRepository.findStaffByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(room_id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setStaff(staff);

        roomRepository.save(room);
        response.put("message", "Add manager successfully!");
        response.put("status", "success");

        return response;
    }

    public RoomDto getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NoSuchElementException("Room not found with ID: " + roomId));
        return mapToDto(room);
    }

    public Page<RoomDto> getAllRooms(Long buildingId, String status,
                                     Double minPrice, Double maxPrice, Integer floor, String roomType,
                                     Integer maxOccupants, Long minAcreage, Long maxAcreage,
                                     int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        UserDto currentUser = userService.getCurrentUser();

        Specification<Room> spec = filterRooms(
                buildingId,
                status,
                minPrice,
                maxPrice,
                floor,
                roomType,
                maxOccupants,
                minAcreage,
                maxAcreage,
                currentUser
        );

        Page<Room> roomPage = roomRepository.findAll(spec, pageable);
        return roomPage.map(this::mapToDto);
    }


    private Specification<Room> filterRooms(Long buildingId, String status, Double minPrice,
                                            Double maxPrice, Integer floor, String roomType,
                                            Integer maxOccupants, Long minAcreage, Long maxAcreage,
                                            UserDto currentUser) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (currentUser == null || currentUser.getRole().toString().equals("CUSTOMER")) {
                predicates.add(builder.isTrue(root.get("isActive")));
                Subquery<Long> contractSubquery = query.subquery(Long.class);
                Root<RentalContract> contractRoot = contractSubquery.from(RentalContract.class);
                contractSubquery.select(contractRoot.get("room").get("roomId"));
                contractSubquery.where(
                        builder.equal(contractRoot.get("customer").get("id"), currentUser.getId())
//                        builder.equal(contractRoot.get("status"), ContractStatus.EXPIRED),
//                        builder.lessThanOrEqualTo(contractRoot.get("startDate"), LocalDate.now()),
//                        builder.greaterThanOrEqualTo(contractRoot.get("endDate"), LocalDate.now())
                );

                predicates.add(root.get("roomId").in(contractSubquery));
            } else if (currentUser.getRole().toString().equals("STAFF")) {
                predicates.add(builder.equal(root.get("staff").get("id"), currentUser.getId()));
                predicates.add(builder.isTrue(root.get("isActive")));
            }

            if (buildingId != null) {
                predicates.add(builder.equal(root.get("building").get("buildingId"), buildingId));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(builder.equal(root.get("status"), RoomStatus.valueOf(status)));
            }

            if (roomType != null && !roomType.isEmpty()) {
                predicates.add(builder.like(root.get("roomType"), "%" + roomType + "%"));
            }

            if (minPrice != null && maxPrice != null) {
                predicates.add(builder.between(root.get("price"), minPrice, maxPrice));
            } else if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            } else if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (floor != null) {
                predicates.add(builder.equal(root.get("floor"), floor));
            }

            if (maxOccupants != null) {
                predicates.add(builder.equal(root.get("maxOccupants"), maxOccupants));
            }

            if (minAcreage != null && maxAcreage != null) {
                predicates.add(builder.between(root.get("acreage"), minAcreage, maxAcreage));
            } else if (minAcreage != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("acreage"), minAcreage));
            } else if (maxAcreage != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("acreage"), maxAcreage));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public Page<RoomDto> getAllRoomsPublic(Long buildingId, String status,
                                           Double minPrice, Double maxPrice, Integer floor, String roomType,
                                           Integer maxOccupants, Long minAcreage, Long maxAcreage,
                                           int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Specification<Room> spec = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Chỉ lấy phòng đang active
            predicates.add(builder.isTrue(root.get("isActive")));

            if (buildingId != null) {
                predicates.add(builder.equal(root.get("building").get("buildingId"), buildingId));
            }

            if (status != null && !status.isEmpty()) {
                predicates.add(builder.equal(root.get("status"), RoomStatus.valueOf(status)));
            }

            if (roomType != null && !roomType.isEmpty()) {
                predicates.add(builder.like(root.get("roomType"), "%" + roomType + "%"));
            }

            if (minPrice != null && maxPrice != null) {
                predicates.add(builder.between(root.get("price"), minPrice, maxPrice));
            } else if (minPrice != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            } else if (maxPrice != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (floor != null) {
                predicates.add(builder.equal(root.get("floor"), floor));
            }

            if (maxOccupants != null) {
                predicates.add(builder.equal(root.get("maxOccupants"), maxOccupants));
            }

            if (minAcreage != null && maxAcreage != null) {
                predicates.add(builder.between(root.get("acreage"), minAcreage, maxAcreage));
            } else if (minAcreage != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("acreage"), minAcreage));
            } else if (maxAcreage != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("acreage"), maxAcreage));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Room> roomPage = roomRepository.findAll(spec, pageable);
        return roomPage.map(this::mapToDto);
    }


    private RentalContractDto toDto(RentalContract contract) {
        return RentalContractDto.builder()
                .id(contract.getContractId())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .rentPrice(contract.getRentPrice())
                .status(contract.getStatus())
                .room_number(contract.getRoom().getRoomNumber())
                .buildingId(contract.getRoom().getBuilding().getBuildingId())
                .pdfFile(contract.getPdfFile())
                .customerId(contract.getCustomer().getId())
                .build();
    }

    private RoomDto mapToDto(Room room) {
        RoomDto.RoomDtoBuilder builder = RoomDto.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .acreage(room.getAcreage())
                .price(room.getPrice())
                .status(room.getStatus().name())
                .roomType(room.getRoomType())
                .floor(room.getFloor())
                .maxOccupants(room.getMaxOccupants())
                .image(room.getImage())
                .description(room.getDescription())
                .isActive(room.getIsActive())
                .buildingId(room.getBuilding() != null ? room.getBuilding().getBuildingId() : null)
                .address(room.getBuilding() != null ? room.getBuilding().getAddress() : null);

        if (room.getStaff() != null) {
            builder.staff(new UserDto(room.getStaff()));
        }
        List<RentalContract> contracts = rentalContractRepository.findByRoom_RoomId(room.getRoomId());
        if (!contracts.isEmpty()) {
            // Lấy hợp đồng hiện tại (IN_PROGRESS)
            contracts.stream()
                    .filter(contract -> contract.getStatus() == ContractStatus.IN_PROGRESS)
                    .findFirst()
                    .ifPresent(contract -> builder.currentContract(toDto(contract)));

            // Lấy lịch sử hợp đồng
            List<RentalContractDto> historyDtos = contracts.stream()
                    .filter(contract -> contract.getStatus() != ContractStatus.IN_PROGRESS)
                    .map(this::toDto)
                    .toList();

            builder.contracts(historyDtos);
        }

        return builder.build();
    }

}
