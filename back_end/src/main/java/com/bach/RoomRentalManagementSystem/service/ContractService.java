package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.RentalContractDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IRentalContractRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ContractService {

    private final IRentalContractRepository rentalContractRepository;
    private final IRoomRepository roomRepository;
    private final IUserRepository userRepository;
    private final UserService userService;

    public Map<String, String> createRentalContractService(RentalContractDto rentalContractDto) {
        Map<String, String> response = new HashMap<>();

        RentalContract contract = new RentalContract();
        mapToRentalContract(rentalContractDto, contract, false);
        rentalContractRepository.save(contract);

        response.put("message", "Rental contract created successfully");
        response.put("status", "success");

        return response;
    }


    public Map<String, String> updateRentalContractService(RentalContractDto rentalContractDto) {
        Map<String, String> response = new HashMap<>();
        RentalContract contract = rentalContractRepository.findById(rentalContractDto.getId())
                .orElseThrow(() -> new RuntimeException("Contract not found!"));

        mapToRentalContract(rentalContractDto, contract, true);

        rentalContractRepository.save(contract);

        response.put("message", "Rental contract updated successfully");
        response.put("status", "success");
        return response;
    }

    public RentalContractDto getContractById(Long id) {
        RentalContract contract = rentalContractRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contract not found"));
        return mapToDto(contract);
    }

//    public RentalContractDto getContractByUserId(Long id) {
//        RentalContract contract = rentalContractRepository.findByUserUserId(id)
//                .orElseThrow(() -> new RuntimeException("Contract not found"));
//        return mapToDto(contract);
//    }

    public Map<String, String> deleteRentalContract(Long contractId) {
        Map<String, String> response = new HashMap<>();
        RentalContract contract = rentalContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found!"));

        rentalContractRepository.delete(contract);

        response.put("message", "Rental contract deleted successfully");
        response.put("status", "success");
        return response;
    }

//    public Page<RentalContractDto> getRentalContractFilter(String room_number, Long building_id, String status, Date startDate, Date endDate, Double minRentPrice, Double maxRentPrice, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Specification<RentalContract> spec = filterRentalContract(room_number, building_id, status, startDate, endDate, minRentPrice, maxRentPrice);
//
//        Page<RentalContract> rentalContractPage = rentalContractRepository.findAll(spec, pageable);
//
//        return rentalContractPage.map(this::mapToDto);
//    }

    public Page<RentalContractDto> getRentalContractFilter(String room_number, Long building_id, String status,
                                                           Date startDate, Date endDate, Double minRentPrice, Double maxRentPrice,
                                                           int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        UserDto currentUser = userService.getCurrentUser();

        Specification<RentalContract> spec = filterRentalContract(room_number, building_id, status, startDate, endDate, minRentPrice, maxRentPrice, currentUser);

        Page<RentalContract> rentalContractPage = rentalContractRepository.findAll(spec, pageable);
        return rentalContractPage.map(this::mapToDto);
    }

//    private Specification<RentalContract> filterRentalContract(String room_number, Long building_id, String status, Date startDate, Date endDate, Double minRentPrice, Double maxRentPrice) {
//        return (root, query, builder) -> {
//            Predicate predicate = builder.conjunction();
//
//            if (room_number != null) {
//                predicate = builder.and(predicate, builder.equal(root.get("room").get("roomNumber"), room_number));
//            }
//
//            if (building_id != null) {
//                predicate = builder.and(predicate, builder.equal(root.get("room").get("building").get("buildingId"), building_id));
//            }
//
//            if (status != null) {
//                predicate = builder.and(predicate, builder.equal(root.get("status"), ContractStatus.valueOf(status)));
//            }
//
//            if (startDate != null && endDate != null) {
//                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("startDate"), startDate));
//                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("endDate"), endDate));
//            } else if (startDate != null) {
//                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("startDate"), startDate));
//            } else if (endDate != null) {
//                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("startDate"), endDate));
//            }
//
//            if (minRentPrice != null && maxRentPrice != null) {
//                predicate = builder.and(predicate, builder.between(root.get("rentPrice"), minRentPrice, maxRentPrice));
//            } else if (minRentPrice != null) {
//                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("rentPrice"), minRentPrice));
//            } else if (maxRentPrice != null) {
//                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("rentPrice"), maxRentPrice));
//            }
//
//            return predicate;
//        };
//    }

    private Specification<RentalContract> filterRentalContract(String room_number, Long building_id, String status,
                                                               Date startDate, Date endDate, Double minRentPrice, Double maxRentPrice,
                                                               UserDto currentUser) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // Nếu là STAFF, chỉ lọc hợp đồng của phòng do họ quản lý
            if (currentUser != null && currentUser.getRole().toString().equals("STAFF")) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("staff").get("id"), currentUser.getId()));
            }

            if (room_number != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("roomNumber"), room_number));
            }

            if (building_id != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("building").get("buildingId"), building_id));
            }

            if (status != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), ContractStatus.valueOf(status)));
            }

            if (startDate != null && endDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("startDate"), startDate));
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("endDate"), endDate));
            } else if (startDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("startDate"), startDate));
            } else if (endDate != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("startDate"), endDate));
            }

            if (minRentPrice != null && maxRentPrice != null) {
                predicate = builder.and(predicate, builder.between(root.get("rentPrice"), minRentPrice, maxRentPrice));
            } else if (minRentPrice != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("rentPrice"), minRentPrice));
            } else if (maxRentPrice != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("rentPrice"), maxRentPrice));
            }

            return predicate;
        };
    }


    public List<ContractStatus> getAllContractStatus() {
        return Arrays.asList(ContractStatus.values());
    }

    @Scheduled(cron = "0 0 0 * * ?")
    //@Scheduled(cron = "*/5 * * * * ?")
    public void updateExpiredContracts() {
        List<RentalContract> contracts = rentalContractRepository.findAll();

        for (RentalContract contract : contracts) {
            if (contract.getEndDate().before(new java.util.Date(System.currentTimeMillis()))) {
                contract.setStatus(ContractStatus.EXPIRED);
                rentalContractRepository.save(contract);
            }
        }
    }

    private void mapToRentalContract(RentalContractDto rentalContractDto, RentalContract contract, boolean isUpdate) {
        contract.setStartDate(rentalContractDto.getStartDate());
        contract.setEndDate(rentalContractDto.getEndDate());
        contract.setRentPrice(rentalContractDto.getRentPrice());
        contract.setStatus(rentalContractDto.getStatus());

        User customer = userRepository.findById(rentalContractDto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + rentalContractDto.getCustomerId()));

        if (!customer.getRole().getRoleName().equals("CUSTOMER")) {
            throw new RuntimeException("User is not a valid customer");
        }

        contract.setCustomer(customer);

        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(rentalContractDto.getBuildingId(), rentalContractDto.getRoom_number())
                .orElseThrow(() -> new RuntimeException("Room not found with room_number: " + rentalContractDto.getRoom_number() +
                        " and buildingId: " + rentalContractDto.getBuildingId()));

        if (!isUpdate) {
            boolean contractExists = rentalContractRepository.existsByRoomAndStatus(room, ContractStatus.IN_PROGRESS);
            if (contractExists) {
                throw new RuntimeException("There is already an active rental contract for this room");
            }
        }

        contract.setRoom(room);
        room.setStatus(RoomStatus.RENTED);
        contract.setPdfFile(rentalContractDto.getPdfFile());
    }

    private RentalContractDto mapToDto(RentalContract rentalContract) {
        return RentalContractDto.builder()
                .id(rentalContract.getContractId())
                .startDate(rentalContract.getStartDate())
                .endDate(rentalContract.getEndDate())
                .rentPrice(rentalContract.getRentPrice())
                .status(rentalContract.getStatus())
                .room_number(rentalContract.getRoom().getRoomNumber())
                .buildingId(rentalContract.getRoom().getBuilding().getBuildingId())
                .customerId(rentalContract.getCustomer().getId())
                .pdfFile(rentalContract.getPdfFile())
                .build();
    }
}
