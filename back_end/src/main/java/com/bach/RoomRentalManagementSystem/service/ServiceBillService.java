package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.ServiceBillDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IRentalContractRepository;
import com.bach.RoomRentalManagementSystem.repository.IServiceBillRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
@RequiredArgsConstructor
public class ServiceBillService {

    private final IServiceBillRepository serviceBillRepository;
    private final IRoomRepository roomRepository;
    private final IRentalContractRepository rentalContractRepository;
    private final NotificationService notificationService;
    private final UserService userService;

    public Map<String, String> createServiceBill(ServiceBillDto serviceBillDto) {
        Map<String, String> response = new HashMap<>();

        ServiceBill serviceBill = new ServiceBill();

        UserDto sender = userService.getCurrentUser();
        Long userId = rentalContractRepository.findUserIdByRoomNumberAndBuildingId(serviceBillDto.getRoom_number(), serviceBillDto.getBuilding_id());

        mapToServiceBill(serviceBillDto, serviceBill);

        serviceBillRepository.save(serviceBill);

        notificationService.sendNotification(
                sender.getId(),
                userId,
                serviceBill.getNote(),
                serviceBill.getBillId(),
                NotiType.SERVICEBILL
        );

        response.put("message", "Service bill created successfully");
        response.put("status", "success");
        return response;
    }

    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "*/5 * * * * ?")
    public void sendRentalBillNotification() {
        List<RentalContract> contracts = rentalContractRepository.findAll();

        for (RentalContract contract : contracts) {
            LocalDate currentDate = LocalDate.now();
            Date currentSqlDate = Date.valueOf(currentDate);

            ServiceBill serviceBill = new ServiceBill();
            serviceBill.setAmount(contract.getRentPrice());
            serviceBill.setService(ServiceBillName.RENT);
            serviceBill.setDate(currentSqlDate);

            Room room = rentalContractRepository.findRoomByContractId(contract.getContractId());
            serviceBill.setRoom(room);
            serviceBillRepository.save(serviceBill);


            LocalDate contractStartDate = contract.getStartDate().toLocalDate();
            LocalDate contractEndDate = contract.getEndDate().toLocalDate();
            int dayOfMonthToCheck = contractStartDate.getDayOfMonth();
            int lastDayOfThisMonth = currentDate.lengthOfMonth();
            int adjustedDay = Math.min(dayOfMonthToCheck, lastDayOfThisMonth);

            if (contractEndDate.getMonth() == currentDate.getMonth() &&
                    contractEndDate.getYear() == currentDate.getYear()) {
                continue;
            }

            if (currentDate.getDayOfMonth() == adjustedDay) {
                LocalDate startPeriod = currentDate.withDayOfMonth(adjustedDay);
                LocalDate endPeriod = currentDate.plusMonths(1).withDayOfMonth(adjustedDay).minusDays(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                String billNotification = "Hoá đơn " + ServiceBillName.RENT.getVietnameseName()
                        + " từ ngày " + startPeriod.format(formatter)
                        + " đến ngày " + endPeriod.format(formatter)
                        + " phòng " + contract.getRoom().getRoomNumber();

                notificationService.sendNotification(
                        null,
                        contract.getCustomer().getId(),
                        billNotification,
                        null,
                        NotiType.SERVICEBILL
                );
            }
        }
    }

    public Map<String, String> updateServiceBill(ServiceBillDto serviceBillDto) {
        Map<String, String> response = new HashMap<>();
        ServiceBill serviceBill = serviceBillRepository.findById(serviceBillDto.getBillId())
                .orElseThrow(() -> new RuntimeException("Service Bill not found!"));

        mapToServiceBill(serviceBillDto, serviceBill);

        serviceBillRepository.save(serviceBill);

        response.put("message", "Service bill updated successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> deleteServiceBill(Long billId) {
        Map<String, String> response = new HashMap<>();
        ServiceBill serviceBill = serviceBillRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Service Bill not found!"));

        serviceBillRepository.delete(serviceBill);

        response.put("message", "Service bill deleted successfully");
        response.put("status", "success");
        return response;
    }

    public ServiceBillDto getServiceBillById(Long billId) {
        ServiceBill serviceBill = serviceBillRepository.findById(billId).orElseThrow(() -> new RuntimeException("Bill not found"));
        return mapToDto(serviceBill);
    }

    public List<ServiceBillName> getAllServicesBillName() {
        return Arrays.asList(ServiceBillName.values());
    }

    public List<BillStatus> getAllBillStatus() {
        return Arrays.asList(BillStatus.values());
    }

    public Page<ServiceBillDto> getServiceBillFilter(Long roomId, Long buildingId, String status, String type, Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<ServiceBill> spec = filterServiceBill(roomId, buildingId, status, type, startDate, endDate);

        Page<ServiceBill> serviceBillPage = serviceBillRepository.findAll(spec, pageable);

        return serviceBillPage.map(this::mapToDto);
    }

    private Specification<ServiceBill> filterServiceBill(Long roomId, Long buildingId, String status, String type, Date startDate, Date endDate) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (roomId != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("roomId"), roomId));
            }

            if (buildingId != null) {
                predicate = builder.and(predicate, builder.equal(root.get("building").get("buildingId"), buildingId));
            }

            if (status != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), BillStatus.valueOf(status)));
            }

            if (type != null) {
                predicate = builder.and(predicate, builder.equal(root.get("service"), ServiceBillName.valueOf(status)));
            }

            if (startDate != null && endDate != null) {
                predicate = builder.and(predicate, builder.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("date"), endDate));
            }

            return predicate;
        };
    }

    private void mapToServiceBill(ServiceBillDto serviceBillDto, ServiceBill serviceBill) {
        serviceBill.setService(serviceBillDto.getService());
        serviceBill.setAmount(serviceBillDto.getAmount());
        serviceBill.setDate(serviceBillDto.getDate());
        serviceBill.setNote(serviceBillDto.getNote());
        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(serviceBillDto.getBuilding_id(), serviceBillDto.getRoom_number())
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        serviceBill.setRoom(room);
    }

    public ServiceBillDto mapToDto(ServiceBill serviceBill) {
        return ServiceBillDto.builder()
                .billId(serviceBill.getBillId())
                .service(serviceBill.getService())
                .amount(serviceBill.getAmount())
                .date(serviceBill.getDate())
                .status(serviceBill.getStatus())
                .room_number(serviceBill.getRoom().getRoomNumber())
                .building_id(serviceBill.getRoom().getBuilding().getBuildingId())
                .build();
    }

    public void updateBillStatus(long id) {
        ServiceBill serviceBill = serviceBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service Bill not found!"));

        serviceBill.setStatus(BillStatus.PAID);
        serviceBillRepository.save(serviceBill);

        Long userId = rentalContractRepository.findUserIdByRoomNumberAndBuildingId(
                serviceBill.getRoom().getRoomNumber(),
                serviceBill.getRoom().getBuilding().getBuildingId()
        );

        // Lấy tên dịch vụ viết thường
        String serviceName = serviceBill.getService().getVietnameseName().toLowerCase();

        // Lấy tháng từ ngày hóa đơn
        String month = String.format("tháng %02d", serviceBill.getDate().toLocalDate().getMonthValue());


        notificationService.sendNotification(
                null,
                userId,
                "Thanh toán thành công hóa đơn " + serviceName
                        + " " + month
                        + " phòng " + serviceBill.getRoom().getRoomNumber(),
                serviceBill.getBillId(),
                NotiType.SERVICEBILL
        );
    }


    private String getMonthYear(Date date) {
        LocalDate localDate = date.toLocalDate();
        return String.format("%02d/%d", localDate.getMonthValue(), localDate.getYear());
    }
}
