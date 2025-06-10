package com.bach.RoomRentalManagementSystem.controller;

import com.bach.RoomRentalManagementSystem.dto.ServiceBillDto;
import com.bach.RoomRentalManagementSystem.model.ContractStatus;
import com.bach.RoomRentalManagementSystem.service.StatisticService;
import jakarta.security.auth.message.callback.PrivateKeyCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Map;
import java.util.TreeMap;

@RestController
public class StatisticController {
    @Autowired
    private StatisticService statisticService;


    @GetMapping("/admin/statistic/service-bill")
    public ResponseEntity<?> calculateTotalIncome(
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            Double response = statisticService.getTotalIncome(startDate, endDate, room_id, building_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/statistic/service-bill/monthly")
    public ResponseEntity<?> calculateTotalIncomePerMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            if (year == null) {
                year = java.time.LocalDate.now().getYear();
            }
            Map<String, Double> monthlyIncome = statisticService.getTotalIncomePerMonth(year, room_id, building_id);
            Map<String, Double> sortedMonthlyIncome = new TreeMap<>(monthlyIncome);
            return ResponseEntity.ok(sortedMonthlyIncome);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/admin/statistic/expenses")
    public ResponseEntity<?> calculateTotalExpenses(
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            Double response = statisticService.getTotalMaintenanceExpenses(startDate, endDate, room_id, building_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/statistic/expenses/monthly")
    public ResponseEntity<?> calculateTotalMaintenanceExpensesPerMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            if (year == null) {
                year = java.time.LocalDate.now().getYear();
            }
            Map<String, Double> response = statisticService.getTotalMaintenanceExpensesPerMonth(year, room_id, building_id);
            Map<String, Double> sortedMonthlyExpenses = new TreeMap<>(response);
            return ResponseEntity.ok(sortedMonthlyExpenses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/admin/statistic/profit")
    public ResponseEntity<?> calculateProfit(
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            Double response = statisticService.getIncomeAndMaintenanceByRoom(startDate, endDate, room_id, building_id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/statistic/profit/monthly")
    public ResponseEntity<?> calculateProfitPerMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            if (year == null) {
                year = java.time.LocalDate.now().getYear();
            }
            Map<String, Double> response = statisticService.getProfitPerMonth(year, room_id, building_id);
            Map<String, Double> sortedMonthlyProfit = new TreeMap<>(response);
            return ResponseEntity.ok(sortedMonthlyProfit);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/admin/statistic/contracts")
    public ResponseEntity<?> calculateContractQuantity(
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) String status) {
        try {
            Integer response = statisticService.getQuantityContracts(startDate, endDate, room_id, building_id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/statistic/contracts/monthly")
    public ResponseEntity<?> calculateContractsPerMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id,
            @RequestParam(required = false) String status) {
        try {
            if (year == null) {
                year = java.time.LocalDate.now().getYear();
            }
            Map<String, Integer> response = statisticService.getContractsPerMonth(year, room_id, building_id, status);
            Map<String, Integer> sortedMonthlyContracts = new TreeMap<>(response);
            return ResponseEntity.ok(sortedMonthlyContracts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/admin/statistic/usage")
    public ResponseEntity<?> calculateUsage(
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id,
            @RequestParam String utilityType) {
        try {
            Double response = statisticService.getTotalUsage(startDate, endDate, room_id, building_id, utilityType);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //    @GetMapping("/auth/statistic/usage/monthly")
//    public ResponseEntity<?> calculateUsagePerMonth(
//            @RequestParam(required = false) Integer year,
//            @RequestParam(required = false) Long room_id,
//            @RequestParam(required = false) Long building_id,
//            @RequestParam String utilityType) {
//        try {
//            if (year == null) {
//                year = java.time.LocalDate.now().getYear();
//            }
//            Map<String, Double> response = statisticService.getTotalUsagePerMonth(year, room_id, building_id, utilityType);
//            Map<String, Double> sortedMonthlyUsage = new TreeMap<>(response);
//            return ResponseEntity.ok(sortedMonthlyUsage);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("error", e.getMessage()));
//        }
//    }
    @GetMapping("/auth/statistic/usage/monthly")
    public ResponseEntity<?> calculateUsagePerMonth(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Long room_id,
            @RequestParam(required = false) Long building_id) {
        try {
            if (year == null) {
                year = java.time.LocalDate.now().getYear();
            }

            Map<String, Map<String, Double>> response = statisticService.getTotalUsagePerMonth(year, room_id, building_id);

            // Sắp xếp theo tháng
            Map<String, Map<String, Double>> sortedMonthlyUsage = new TreeMap<>(response);

            return ResponseEntity.ok(sortedMonthlyUsage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }


}
