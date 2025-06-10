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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface IRoomUtilityRepository extends JpaRepository<RoomUtility, Long>, JpaSpecificationExecutor<RoomUtility> {
    Page<RoomUtility> findAll(Specification<RoomUtility> spec, Pageable pageable);

    RoomUtility findTopByRoomAndDateBeforeOrderByDateDesc(Room room,  Date date);
    RoomUtility findTopByRoomAndDateOrderByDateDesc(Room room, Date date);

    @Query("SELECT r.roomId FROM Room r WHERE r.staff.id = :staffId AND r.isActive = true")
    List<Long> findRoomIdsByManagerId(@Param("staffId") Long staffId);
}
