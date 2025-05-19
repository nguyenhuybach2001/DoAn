package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IMaintenanceExpensesRepository;
import com.bach.RoomRentalManagementSystem.repository.IRentalContractRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomUtilityRepository;
import com.bach.RoomRentalManagementSystem.repository.IServiceBillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class StatisticService {

    private final IServiceBillRepository serviceBillRepository;
    private final IMaintenanceExpensesRepository maintenanceExpensesRepository;
    private final IRentalContractRepository rentalContractRepository;
    private final IRoomUtilityRepository roomUtilityRepository;

    // Thống kê tổng tiền thu từ hóa đơn dịch vụ trong khoảng thời gian
    public Double getTotalIncome(Date startDate, Date endDate, Long room_id, Long building_id) {
        Specification<ServiceBill> spec = StatisticSpecificationsService.serviceBillHasPaidStatusAndDateBetween(startDate, endDate, room_id, building_id);
        List<ServiceBill> serviceBills = serviceBillRepository.findAll(spec);

        return serviceBills.stream()
                .mapToDouble(ServiceBill::getAmount)
                .sum();
    }

    // Tiền thu được theo tháng
    public Map<String, Double> getTotalIncomePerMonth(int year, Long room_id, Long building_id) {
        Map<String, Double> monthlyIncome = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            // Tính startDate và endDate cho tháng hiện tại
            Date startDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-01");
            Date endDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-31");

            Specification<ServiceBill> spec = StatisticSpecificationsService.serviceBillHasPaidStatusAndDateBetween(startDate, endDate, room_id, building_id);
            List<ServiceBill> serviceBills = serviceBillRepository.findAll(spec);

            double totalIncome = serviceBills.stream()
                    .mapToDouble(ServiceBill::getAmount)
                    .sum();

            monthlyIncome.put(String.format("%02d", month), totalIncome);
        }
        return monthlyIncome;
    }




    // Thống kê tổng chi phí bảo trì trong khoảng thời gian
    public Double getTotalMaintenanceExpenses(Date startDate, Date endDate, Long room_id, Long building_id) {
        Specification<MaintenanceExpenses> spec = StatisticSpecificationsService.expensesHasPaidStatusAndDateBetween(startDate, endDate, room_id, building_id);
        List<MaintenanceExpenses> maintenanceExpenses = maintenanceExpensesRepository.findAll(spec);

        return maintenanceExpenses.stream()
                .mapToDouble(MaintenanceExpenses::getAmount)
                .sum();
    }

    // Chi phí bảo trì theo tháng
    public Map<String, Double> getTotalMaintenanceExpensesPerMonth(int year, Long room_id, Long building_id) {
        Map<String, Double> monthlyExpenses = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            // Tính startDate và endDate cho tháng hiện tại
            Date startDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-01");
            Date endDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-31");

            Specification<MaintenanceExpenses> spec = StatisticSpecificationsService.expensesHasPaidStatusAndDateBetween(startDate, endDate, room_id, building_id);
            List<MaintenanceExpenses> maintenanceExpenses = maintenanceExpensesRepository.findAll(spec);

            double totalExpenses = maintenanceExpenses.stream()
                    .mapToDouble(MaintenanceExpenses::getAmount)
                    .sum();

            monthlyExpenses.put(String.format("%02d", month), totalExpenses);
        }
        return monthlyExpenses;
    }



    // Thống kê tổng thu nhập và chi phí bảo trì theo phòng
    public Double getIncomeAndMaintenanceByRoom(Date startDate, Date endDate, Long room_id, Long building_id) {
        // Thu nhập từ hóa đơn dịch vụ
        Double totalIncome = getTotalIncome(startDate, endDate, room_id, building_id);

        // Chi phí bảo trì
        Double totalExpenses = getTotalMaintenanceExpenses(startDate, endDate, room_id, building_id);

        return totalIncome - totalExpenses; // Tổng thu nhập trừ chi phí bảo trì
    }

    // Lợi nhuận theo tháng
    public Map<String, Double> getProfitPerMonth(int year, Long room_id, Long building_id) {
        Map<String, Double> monthlyProfit = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            // Tính startDate và endDate cho tháng hiện tại
            Date startDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-01");
            Date endDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-31");

            // Tính tổng tiền thu được
            double totalIncome = getTotalIncome(startDate, endDate, room_id, building_id);

            // Tính tổng chi phí bảo trì
            double totalExpenses = getTotalMaintenanceExpenses(startDate, endDate, room_id, building_id);

            // Tính lợi nhuận
            double profit = totalIncome - totalExpenses;

            monthlyProfit.put(String.format("%02d", month), profit);
        }
        return monthlyProfit;
    }

    // Thống kê số hợp đồng
    public Integer getQuantityContracts(Date startDate, Date endDate, Long room_id, Long building_id, String status) {
        Specification<RentalContract> spec = StatisticSpecificationsService.rentalContractSpecification(startDate, endDate, room_id, building_id, ContractStatus.valueOf(status));
        List<RentalContract> rentalContracts = rentalContractRepository.findAll(spec);
        return rentalContracts.size();
    }

    // Số hợp đồng theo tháng
    public Map<String, Integer> getContractsPerMonth(int year, Long room_id, Long building_id, String status) {
        Map<String, Integer> monthlyContracts = new HashMap<>();

        ContractStatus contractStatus = ContractStatus.valueOf(status);

        for (int month = 1; month <= 12; month++) {
            Date startDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-01");
            Date endDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-31");

            Specification<RentalContract> spec = StatisticSpecificationsService.rentalContractSpecification(startDate, endDate, room_id, building_id, contractStatus);
            List<RentalContract> rentalContracts = rentalContractRepository.findAll(spec);
            monthlyContracts.put(String.format("%02d", month), rentalContracts.size());
        }

        return monthlyContracts;
    }



    // Thống kê điện nước tiêu thụ
    public Double getTotalUsage(Date startDate, Date endDate, Long room_id, Long building_id, String utilityType) {
        Specification<RoomUtility> spec = StatisticSpecificationsService.totalUsage(startDate, endDate, room_id, building_id, RoomUtilityType.valueOf(utilityType));
        List<RoomUtility> roomUtilities = roomUtilityRepository.findAll(spec);

        return roomUtilities.stream()
                .mapToDouble(RoomUtility::getUsage)
                .sum();
    }


    // Số điện nước tiêu thụ theo tháng
    public Map<String, Double> getTotalUsagePerMonth(int year, Long room_id, Long building_id, String utilityType) {
        Map<String, Double> monthlyUsage = new HashMap<>();

        for (int month = 1; month <= 12; month++) {
            // Tính startDate và endDate cho tháng hiện tại
            Date startDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-01");
            Date endDate = Date.valueOf(year + "-" + String.format("%02d", month) + "-31");

            Specification<RoomUtility> spec = StatisticSpecificationsService.totalUsage(startDate, endDate, room_id, building_id, RoomUtilityType.valueOf(utilityType));
            List<RoomUtility> roomUtilities = roomUtilityRepository.findAll(spec);

            double totalUsage = roomUtilities.stream()
                    .mapToDouble(RoomUtility::getUsage)
                    .sum();

            monthlyUsage.put(String.format("%02d", month), totalUsage);
        }
        return monthlyUsage;
    }

}
