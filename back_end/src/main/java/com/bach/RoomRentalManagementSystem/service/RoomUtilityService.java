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

        roomUtilityRepository.saveAll(roomUtilities);

        for (RoomUtility roomUtility : roomUtilities) {
            createBillForUtility(roomUtility);
        }

        response.put("message", "Room utility created successfully");
        response.put("status", "success");
        return response;
    }

    private void createBillForUtility(RoomUtility roomUtility) {
        Room room = roomUtility.getRoom();
        Building building = room.getBuilding();
        RoomUtilityType utilityType = roomUtility.getUtilityType();
        Date utilityDate = roomUtility.getDate();

        RoomUtility lastUtility = roomUtilityRepository
                .findTopByRoomAndUtilityTypeAndDateBeforeOrderByDateDesc(room, utilityType, utilityDate);

        if (lastUtility == null) {
            return;
        }

        double usageDifference = roomUtility.getUsage() - lastUtility.getUsage();
        if (usageDifference <= 0) {
            return;
        }

        ServiceBill bill = new ServiceBill();
        bill.setRoom(room);
        bill.setService(utilityType == RoomUtilityType.ELECTRICITY ? ServiceBillName.ELECTRIC : ServiceBillName.WATER);
        bill.setAmount(utilityType == RoomUtilityType.ELECTRICITY
                ? usageDifference * (building.getElectricityPrice() != null ? building.getElectricityPrice() : 3800)
                : (building.getWaterPrice() != null ? building.getWaterPrice() : 8000));
        bill.setDate(utilityDate);
        bill.setStatus(BillStatus.UNPAID);
        bill.setNote("Hóa đơn " + utilityType.getVietnameseName() + " từ "
                + lastUtility.getDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " đến " + utilityDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        serviceBillRepository.save(bill);

        Long userId = rentalContractRepository.findUserIdByRoomNumberAndBuildingId(bill.getRoom().getRoomNumber(), bill.getRoom().getBuilding().getBuildingId());

        notificationService.sendNotification(
                null,
                userId,
                bill.getNote(),
                bill.getBillId(),
                NotiType.SERVICEBILL
        );
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

    public Page<RoomUtilityDto> getRoomUtilityFilter(String room_number, Long building_id, Date startDate, Date endDate, Double minUsage, Double maxUsage, String utilityType , int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<RoomUtility> spec = filterRoomUtility(room_number, building_id, startDate, endDate, minUsage, maxUsage, utilityType );

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

            if (utilityType != null) {
                predicate = builder.and(predicate, builder.equal(root.get("utilityType"), RoomUtilityType.valueOf(utilityType)));
            }

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
        roomUtility.setUsage(roomUtilityDto.getUsage());
        roomUtility.setUtilityType(roomUtilityDto.getUtilityType());
        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(roomUtilityDto.getBuilding_id(), roomUtilityDto.getRoom_number())
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        roomUtility.setRoom(room);
    }

    private RoomUtilityDto mapToDto(RoomUtility roomUtility) {
        return RoomUtilityDto.builder()
                .id(roomUtility.getUtility_id())
                .date(roomUtility.getDate())
                .usage(roomUtility.getUsage())
                .utilityType(roomUtility.getUtilityType())
                .room_number(roomUtility.getRoom().getRoomNumber())
                .building_id(roomUtility.getRoom().getBuilding().getBuildingId())
                .build();
    }
}
