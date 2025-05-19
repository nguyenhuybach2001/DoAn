package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.ExpensesImportDto;
import com.bach.RoomRentalManagementSystem.dto.MaintenanceExpensesDto;
import com.bach.RoomRentalManagementSystem.dto.RentalContractDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.*;
import com.sun.tools.javac.Main;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MaintenanceExpensesService {

    private final IMaintenanceExpensesRepository repository;
    private final IAssetInRoomRepository assetInRoomRepository;
    private final IAssetOfBuildingRepository assetOfBuildingRepository;
    private final INotificationRepository notificationRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final NotificationService notificationService;
    private final AssetInRoomService assetInRoomService;
    private final AssetOfBuildingService assetOfBuildingService;
    private final UserService userService;


    public Map<String, String> createExpense(ExpensesImportDto dto) {
        MaintenanceExpenses expense = mapToEntity(dto, new MaintenanceExpenses());
        repository.save(expense);
        UserDto sender = userService.getCurrentUser();
        User admin = userRepository.findByRole(roleRepository.findByRoleName(RoleName.LANDLORD));

        notificationService.sendNotification(
                sender.getId(),
                admin.getId(),
                "Bạn có 1 hóa đơn cần xác nhận!",
                expense.getExpensesId(),
                NotiType.MAINTENANCEEXPENSE
        );

        return Map.of("message", "Expense created successfully", "status", "success");
    }

    public Map<String, String> updateExpense(ExpensesImportDto dto) {
        MaintenanceExpenses existing = repository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Expense not found!"));
        mapToEntity(dto, existing);
        repository.save(existing);
        return Map.of("message", "Expense updated successfully", "status", "success");
    }

    public Map<String, String> setApprovalStatuses(Long id, ApprovalStatuses approvalStatuses) {
        MaintenanceExpenses existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found!"));
        existing.setApprovalStatuses(approvalStatuses);
        repository.save(existing);

        String message = "";

        if (approvalStatuses == ApprovalStatuses.APPROVED) {
            message = "Yêu cầu của bạn đã được chấp nhận!";
        } else if (approvalStatuses == ApprovalStatuses.REJECTED) {
            message = "Yêu cầu của bạn đã bị từ chối!";
        } else {
            throw new RuntimeException("sai status");
        }

        User admin = userRepository.findByRole(roleRepository.findByRoleName(RoleName.LANDLORD));

        Long recipientId = notificationRepository.findSenderIdByServiceIdAndSenderId(existing.getExpensesId())
                .orElseThrow(() -> new RuntimeException("staff id not found"));

        notificationService.sendNotification(
                admin.getId(),
                recipientId,
                message,
                existing.getExpensesId(),
                NotiType.MAINTENANCEEXPENSE
        );

        return Map.of("message", "Expense updated successfully", "status", "success");
    }

    public Map<String, String> deleteExpense(Long id) {
        MaintenanceExpenses expense = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found!"));
        repository.delete(expense);
        return Map.of("message", "Expense deleted successfully", "status", "success");
    }

    public MaintenanceExpensesDto getExpenseById(Long id) {
        MaintenanceExpenses expense = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found!"));
        return mapToDto(expense);
    }

    public List<ExpenseType> getExpenseType() {
        return Arrays.asList(ExpenseType.values());
    }

    public List<ApprovalStatuses> getApprovalStatuses() {
        return Arrays.asList(ApprovalStatuses.values());
    }

    public Page<MaintenanceExpensesDto> getExpensesFilter(String expenseType, Date startDate, Date endDate, Double minAmount, Double maxAmount, String status, String approvalStatuses, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<MaintenanceExpenses> spec = filterExpenses(expenseType, startDate, endDate, minAmount, maxAmount, status, approvalStatuses);

        Page<MaintenanceExpenses> expensesPage = repository.findAll(spec, pageable);

        return expensesPage.map(this::mapToDto);
    }

    private Specification<MaintenanceExpenses> filterExpenses(String expenseType, Date startDate, Date endDate, Double minAmount, Double maxAmount, String status, String approvalStatuses) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            if (expenseType != null) {
                predicate = builder.and(predicate, builder.equal(root.get("expenseType"), ExpenseType.valueOf(expenseType)));
            }

            if (status != null) {
                predicate = builder.and(predicate, builder.equal(root.get("billStatus"), BillStatus.valueOf(status)));
            }

            if (approvalStatuses != null) {
                predicate = builder.and(predicate, builder.equal(root.get("approvalStatuses"), ApprovalStatuses.valueOf(approvalStatuses)));
            }

            if (startDate != null && endDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("startDate"), startDate));
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("endDate"), endDate));
            } else if (startDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("startDate"), startDate));
            } else if (endDate != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("startDate"), endDate));
            }

            if (minAmount != null && maxAmount != null) {
                predicate = builder.and(predicate, builder.between(root.get("amount"), minAmount, maxAmount));
            } else if (minAmount != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("amount"), minAmount));
            } else if (maxAmount != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            return predicate;
        };
    }

    private MaintenanceExpenses mapToEntity(ExpensesImportDto dto, MaintenanceExpenses entity) {


        if (dto.getAsset_in_room_id() != null) {
            AssetInRoom assetInRoom = assetInRoomRepository.findById(dto.getAsset_in_room_id())
                    .orElseThrow(() -> new RuntimeException("AssetInRoom not found!"));
            entity.setAssetInRoom(assetInRoom);
        } else {
            entity.setAssetInRoom(null);
        }

        if (dto.getAsset_of_building_id() != null) {
            AssetOfBuilding assetOfBuilding = assetOfBuildingRepository.findById(dto.getAsset_of_building_id())
                    .orElseThrow(() -> new RuntimeException("AssetOfBuilding not found!"));
            entity.setAssetOfBuilding(assetOfBuilding);
        } else {
            entity.setAssetOfBuilding(null);
        }

        entity.setExpenseType(dto.getExpense_type());
        entity.setDescription(dto.getDescription());
        entity.setAmount(dto.getAmount());
        entity.setDate(dto.getDate());
        entity.setNote(dto.getNote());

        return entity;
    }

    private MaintenanceExpensesDto mapToDto(MaintenanceExpenses entity) {
        return MaintenanceExpensesDto.builder()
                .id(entity.getExpensesId())
                .assetInRoomDto(entity.getAssetInRoom() != null ? assetInRoomService.mapToDto(entity.getAssetInRoom()) : null)
                .assetOfBuildingDto(entity.getAssetOfBuilding() != null ? assetOfBuildingService.mapToDto(entity.getAssetOfBuilding()) : null)
                .expense_type(entity.getExpenseType())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .date(entity.getDate())
                .status(entity.getBillStatus())
                .approval(entity.getApprovalStatuses())
                .note(entity.getNote())
                .build();
    }
}
