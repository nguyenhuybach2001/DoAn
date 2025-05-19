package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.ContractStatus;
import com.bach.RoomRentalManagementSystem.model.MaintenanceExpenses;
import com.bach.RoomRentalManagementSystem.model.RentalContract;
import com.bach.RoomRentalManagementSystem.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IRentalContractRepository extends JpaRepository<RentalContract, Long>, JpaSpecificationExecutor<RentalContract> {
    Page<RentalContract> findAll(Specification<RentalContract> spec, Pageable pageable);

    boolean existsByRoomAndStatus(Room room, ContractStatus contractStatus);

    List<RentalContract> findByRoom_RoomId(Long roomId);

    @Query("SELECT rc.room FROM RentalContract rc WHERE rc.contractId = :contractId AND rc.status = 'IN_PROGRESS'")
    Room findRoomByContractId(@Param("contractId") Long contractId);

    Optional<RentalContract> findByRoom_RoomIdAndStatus(Long roomId, ContractStatus status);

    @Query("""
                SELECT rc.customer.id
                FROM RentalContract rc
                WHERE rc.room.roomNumber = :roomNumber
                  AND rc.room.building.buildingId = :buildingId
                  AND rc.status = 'IN_PROGRESS'
            """)
    Long findUserIdByRoomNumberAndBuildingId(String roomNumber, Long buildingId);

    @Query("""
                    SELECT r.roomNumber, r.building.name
                    FROM RentalContract rc
                    JOIN rc.room r
                    JOIN r.building b
                    WHERE rc.customer.id = :userId
                        AND rc.status = 'IN_PROGRESS'
            """)
    List<Object[]> findRoomNumberAndBuildingNameByUserId(Long userId);

    @Query("SELECT r.contractId FROM RentalContract r WHERE r.customer.id = :userId")
    List<Long> findContractIdsByUserId(@Param("userId") Long userId);
}
