package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface IBuildingRepository extends JpaRepository<Building, Long> {

    Building findByBuildingId(Long id);

    List<Building> findAllByIsActive(Boolean isAdmin);

    //List<Building> findByIsActiveTrueAndStaffs_Id(Long staffId);

    //@Query("SELECT bs.building.buildingId FROM BuildingStaff bs WHERE bs.user.email = :email")
    //List<Long> findBuildingIdsByStaffEmail(@Param("email") String email);

}
