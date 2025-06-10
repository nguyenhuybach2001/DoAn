package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.RoomUtilityDto;
import com.bach.RoomRentalManagementSystem.dto.ServiceBillDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IRentalContractRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomUtilityRepository;
import com.bach.RoomRentalManagementSystem.repository.IServiceBillRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class RoomUtilityService {

    private final IRoomUtilityRepository roomUtilityRepository;
    private final IRoomRepository roomRepository;
    private final IRentalContractRepository rentalContractRepository;
    private final IServiceBillRepository serviceBillRepository;
    private final NotificationService notificationService;

    public Map<String, String> createRoomUtilityService(List<RoomUtilityDto> roomUtilityDtoList) {
        Map<String, String> response = new HashMap<>();
        List<RoomUtility> roomUtilities = new ArrayList<>();

        for (RoomUtilityDto roomUtilityDto : roomUtilityDtoList) {
            RoomUtility roomUtility = new RoomUtility();
            mapToRoomUtility(roomUtilityDto, roomUtility);
            roomUtilities.add(roomUtility);
        }

        List<RoomUtility> savedUtilities = roomUtilityRepository.saveAll(roomUtilities);
        roomUtilityRepository.flush(); // đảm bảo các bản ghi đã được commit

        for (RoomUtility saved : savedUtilities) {
            System.out.println("Creating bill for Room ID: " + saved.getRoom().getRoomId());
            createBillForUtility(saved);
        }
//        for (RoomUtilityDto dto : roomUtilityDtoList) {
//            Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(dto.getBuilding_id(), dto.getRoom_number())
//                    .orElseThrow(() -> new RuntimeException("Room not found!"));
//            RoomUtility savedUtility = roomUtilityRepository
//                    .findTopByRoomAndDateOrderByDateDesc(room, dto.getDate());
//            if (savedUtility != null) {
//                System.out.println("Creating bill for Room ID: " + room.getRoomId());
//                createBillForUtility(savedUtility);
//            } else {
//                System.out.println("Utility not found for Room ID: " + room.getRoomId() + " at date " + dto.getDate());
//            }
//        }

        response.put("message", "Room utility created successfully");
        response.put("status", "success");
        return response;
    }

//    private void createBillForUtility(RoomUtility roomUtility) {
//        Room room = roomUtility.getRoom();
//        Building building = room.getBuilding();
//        Date utilityDate = roomUtility.getDate();
//
//        RoomUtility lastUtility = roomUtilityRepository
//                .findTopByRoomAndDateBeforeOrderByDateDesc(room, utilityDate);
//
//        if (lastUtility == null) {
//            return;
//        }
//        Double lastElectric = lastUtility.getUsage_electricity();
//        Double currentElectric = roomUtility.getUsage_electricity();
//        Double lastWater = lastUtility.getUsage_water();
//        Double currentWater = roomUtility.getUsage_water();
//
//        double electricDiff = (currentElectric != null && lastElectric != null) ? currentElectric - lastElectric : 0;
//        double waterDiff = (currentWater != null && lastWater != null) ? currentWater - lastWater : 0;
//
//        if (electricDiff <= 0 && waterDiff <= 0) {
//            return; // không có gì để tính
//        }
//
//        double electricityPrice = building.getElectricityPrice() != null ? building.getElectricityPrice() : 3800;
//        double waterPrice = building.getWaterPrice() != null ? building.getWaterPrice() : 8000;
//
//        double totalAmount = (electricDiff > 0 ? electricDiff * electricityPrice : 0)
//                + (waterDiff > 0 ? waterDiff * waterPrice : 0);
//
//
//        ServiceBill bill = new ServiceBill();
//        bill.setRoom(room);
//        bill.setService(ServiceBillName.ELECTRICITY_AND_WATER);
//        bill.setAmount(totalAmount);
//        bill.setDate(utilityDate);
//        bill.setStatus(BillStatus.UNPAID);
//        bill.setNote("Hóa đơn điện nước từ "
//                + lastUtility.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
//                + " đến " + utilityDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//
//        serviceBillRepository.save(bill);
//
//        Long userId = rentalContractRepository.findUserIdByRoomNumberAndBuildingId(bill.getRoom().getRoomNumber(), bill.getRoom().getBuilding().getBuildingId());
//
//        notificationService.sendNotification(
//                null,
//                userId,
//                bill.getNote(),
//                bill.getBillId(),
//                NotiType.SERVICEBILL
//        );
//    }

    private void createBillForUtility(RoomUtility roomUtility) {
        Room room = roomUtility.getRoom();
        Building building = room.getBuilding();
        Date utilityDate = roomUtility.getDate();

        Double currentElectric = roomUtility.getUsage_electricity() != null ? roomUtility.getUsage_electricity() : 0.0;
        Double currentWater = roomUtility.getUsage_water() != null ? roomUtility.getUsage_water() : 0.0;

        // Nếu usage âm hoặc không tăng, không tạo hóa đơn
        if (currentElectric <= 0 && currentWater <= 0) {
            System.out.println("⚠️ Không có usage hợp lệ. Bỏ qua Room: " + room.getRoomNumber());
            return;
        }

        double electricityPrice = building.getElectricityPrice() != null ? building.getElectricityPrice() : 3800;
        double waterPrice = building.getWaterPrice() != null ? building.getWaterPrice() : 8000;

        double totalAmount = currentElectric * electricityPrice + currentWater * waterPrice;

        ServiceBill bill = new ServiceBill();
        bill.setRoom(room);
        bill.setService(ServiceBillName.ELECTRICITY_AND_WATER);
        bill.setAmount(totalAmount);
        bill.setDate(utilityDate);
        bill.setStatus(BillStatus.UNPAID);

        String month = utilityDate.toLocalDate().format(DateTimeFormatter.ofPattern("MM"));
        String roomNumber = room.getRoomNumber();
        String note = "Hóa đơn tiền điện nước tháng " + month + " của phòng " + roomNumber;
        bill.setNote(note);

        serviceBillRepository.saveAndFlush(bill);

        Long userId = rentalContractRepository.findUserIdByRoomNumberAndBuildingId(
                room.getRoomNumber(),
                room.getBuilding().getBuildingId()
        );
        if (userId != null) {
            notificationService.sendNotification(
                    null,
                    userId,
                    note,
                    bill.getBillId(),
                    NotiType.SERVICEBILL
            );
        }
    }


    public Map<String, String> updateRoomUtilityService(RoomUtilityDto roomUtilityDto) {
        Map<String, String> response = new HashMap<>();

        RoomUtility utility = roomUtilityRepository.findById(roomUtilityDto.getId())
                .orElseThrow(() -> new RuntimeException("Room utility not found for ID: " + roomUtilityDto.getId()));

        mapToRoomUtility(roomUtilityDto, utility);

        roomUtilityRepository.save(utility);


        response.put("message", "Room utility updated successfully");
        response.put("status", "success");
        return response;
    }

    public RoomUtilityDto getRoomUtilityById(Long id) {
        RoomUtility utility = roomUtilityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return mapToDto(utility);
    }

    public Page<RoomUtilityDto> getRoomUtilityFilter(String room_number, Long building_id, Date startDate, Date endDate, Double minUsage, Double maxUsage, String utilityType, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<RoomUtility> spec = filterRoomUtility(room_number, building_id, startDate, endDate, minUsage, maxUsage, utilityType);

        Page<RoomUtility> roomUtilityPage = roomUtilityRepository.findAll(spec, pageable);

        return roomUtilityPage.map(this::mapToDto);
    }

    private Specification<RoomUtility> filterRoomUtility(String room_number, Long building_id, Date startDate, Date endDate, Double minUsage, Double maxUsage, String utilityType) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            predicate = builder.and(predicate, builder.isTrue(root.get("room").get("isActive")));
            predicate = builder.and(predicate, builder.isTrue(root.get("room").get("building").get("isActive")));

            if (room_number != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("roomNumber"), room_number));
            }

            if (building_id != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("building").get("buildingId"), building_id));
            }

//            if (utilityType != null) {
//                predicate = builder.and(predicate, builder.equal(root.get("utilityType"), RoomUtilityType.valueOf(utilityType)));
//            }

            if (startDate != null && endDate != null) {
                predicate = builder.and(predicate, builder.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (minUsage != null && maxUsage != null) {
                predicate = builder.and(predicate, builder.between(root.get("electricityUsage"), minUsage, maxUsage));
            } else if (minUsage != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("electricityUsage"), minUsage));
            } else if (maxUsage != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("electricityUsage"), maxUsage));
            }

            return predicate;
        };
    }

    private void mapToRoomUtility(RoomUtilityDto roomUtilityDto, RoomUtility roomUtility) {
        roomUtility.setDate(roomUtilityDto.getDate());
        roomUtility.setUsage_electricity(roomUtilityDto.getUsage_electricity());
        roomUtility.setUsage_water(roomUtilityDto.getUsage_water());
        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(roomUtilityDto.getBuilding_id(), roomUtilityDto.getRoom_number())
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        roomUtility.setRoom(room);
    }

    private RoomUtilityDto mapToDto(RoomUtility roomUtility) {
        return RoomUtilityDto.builder()
                .id(roomUtility.getUtility_id())
                .date(roomUtility.getDate())
                .usage_water(roomUtility.getUsage_water())
                .usage_electricity(roomUtility.getUsage_electricity())
                .room_number(roomUtility.getRoom().getRoomNumber())
                .building_id(roomUtility.getRoom().getBuilding().getBuildingId())
                .build();
    }
}
