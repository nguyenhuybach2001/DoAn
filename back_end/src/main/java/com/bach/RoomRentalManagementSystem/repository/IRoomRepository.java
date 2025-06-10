package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.Room;
import com.bach.RoomRentalManagementSystem.model.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IRoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    Page<Room> findByBuildingBuildingId(Long buildingId, Pageable pageable);

    List<Room> findByBuilding_BuildingId(Long buildingId);

    @Query("SELECT r FROM Room r WHERE r.building.buildingId = :buildingId AND r.roomNumber = :roomNumber AND r.isActive = true AND r.building.isActive = true")
    Optional<Room> findByBuildingBuildingIdAndRoomNumber(@Param("buildingId") Long buildingId, @Param("roomNumber") String roomNumber);

    boolean existsByRoomNumberAndBuilding_BuildingIdAndIsActive(String roomNumber, Long buildingId, boolean isActive);

    int countByBuilding_BuildingId(Long buildingId);

    @Query("SELECT r.roomNumber FROM Room r WHERE r.building.buildingId = :buildingId AND r.isActive = true")
    List<String> findActiveRoomNumbersInBuilding(@Param("buildingId") Long buildingId);


}
