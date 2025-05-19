package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.AssetInRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IAssetInRoomRepository extends JpaRepository<AssetInRoom, Long> {
//    Page<AssetInRoom> findByRoomRoomIdAndRoomBuildingId(Long roomId, Long buildingId, Pageable pageable);
//    Page<AssetInRoom> findByRoomBuildingId(Long buildingId, Pageable pageable);

    Page<AssetInRoom> findAll(Specification<AssetInRoom> spec, Pageable pageable);
    Optional<AssetInRoom> findByRoom_RoomNumberAndRoom_Building_BuildingId(String roomNumber, Long buildingId);
}
