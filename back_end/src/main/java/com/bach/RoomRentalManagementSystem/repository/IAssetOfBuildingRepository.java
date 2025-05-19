package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.AssetOfBuilding;
import com.bach.RoomRentalManagementSystem.model.MaintenanceExpenses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAssetOfBuildingRepository extends JpaRepository<AssetOfBuilding, Long> {
    Page<AssetOfBuilding> findAll(Specification<AssetOfBuilding> spec, Pageable pageable);
    Optional<AssetOfBuilding> findByBuildingBuildingId(Long buildingId);
}
