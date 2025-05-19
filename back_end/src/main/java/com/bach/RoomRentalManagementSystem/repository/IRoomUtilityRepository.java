package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.MaintenanceExpenses;
import com.bach.RoomRentalManagementSystem.model.Room;
import com.bach.RoomRentalManagementSystem.model.RoomUtility;
import com.bach.RoomRentalManagementSystem.model.RoomUtilityType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.sql.Date;

public interface IRoomUtilityRepository extends JpaRepository<RoomUtility, Long>, JpaSpecificationExecutor<RoomUtility> {
    Page<RoomUtility> findAll(Specification<RoomUtility> spec, Pageable pageable);

    RoomUtility findTopByRoomAndUtilityTypeAndDateBeforeOrderByDateDesc(Room room, RoomUtilityType utilityType, Date date);
}
