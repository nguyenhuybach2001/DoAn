package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.model.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticSpecificationsService {

    public static Specification<ServiceBill> serviceBillHasPaidStatusAndDateBetween(Date startDate, Date endDate, Long room_id, Long building_id) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), BillStatus.PAID));

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (room_id !=null && building_id != null) {
                throw new RuntimeException("Không được truyền đồng thời room_id và building_id");
            }

            if (room_id != null) {
                predicates.add(cb.equal(root.get("room").get("roomId"), room_id));
            }

            if (building_id != null) {
                predicates.add(cb.equal(root.get("room").get("building").get("buildingId"), building_id));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<MaintenanceExpenses> expensesHasPaidStatusAndDateBetween(Date startDate, Date endDate, Long room_id, Long building_id) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("billStatus"), BillStatus.PAID));

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (room_id !=null && building_id != null) {
                throw new RuntimeException("Không được truyền đồng thời room_id và building_id");
            }

            if (room_id != null) {
                predicates.add(cb.equal(root.get("assetInRoom").get("room").get("roomId"), room_id));
            }

            if (building_id != null) {
                predicates.add(cb.equal(root.get("assetOfBuilding").get("building").get("buildingId"), building_id));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<RentalContract> rentalContractSpecification(Date startDate, Date endDate, Long room_id, Long building_id, ContractStatus contractStatus) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (contractStatus != null) {
                predicates.add(cb.equal(root.get("status"), contractStatus));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (room_id !=null && building_id != null) {
                throw new RuntimeException("Không được truyền đồng thời room_id và building_id");
            }

            if (room_id != null) {
                predicates.add(cb.equal(root.get("room").get("roomId"), room_id));
            }

            if (building_id != null) {
                predicates.add(cb.equal(root.get("room").get("building").get("buildingId"), building_id));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<RoomUtility> totalUsage(Date startDate, Date endDate, Long room_id, Long building_id, RoomUtilityType utilityType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("utilityType"), utilityType));

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (room_id !=null && building_id != null) {
                throw new RuntimeException("Không được truyền đồng thời room_id và building_id");
            }

            if (room_id != null) {
                predicates.add(cb.equal(root.get("room").get("roomId"), room_id));
            }

            if (building_id != null) {
                predicates.add(cb.equal(root.get("room").get("building").get("buildingId"), building_id));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
