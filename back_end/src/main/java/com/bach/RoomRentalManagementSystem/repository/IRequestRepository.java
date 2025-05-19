package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.Request;
import com.bach.RoomRentalManagementSystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IRequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAll(Specification<Request> spec, Pageable pageable);

    @Query("""
        SELECT rc.customer
        FROM Request req
        JOIN req.room r
        JOIN r.rentalContracts rc
        WHERE req.rqId = :requestId
        AND rc.status = 'IN_PROGRESS'
        """)
    User findUserByRequestIdAndStatus(Long requestId);

    @Query("SELECT r.staff FROM Room r WHERE r.roomNumber = :roomNumber AND r.building.buildingId = :buildingId")
    User findManagerByRoomNumberAndBuildingId(@Param("roomNumber") String roomNumber,
                                              @Param("buildingId") Long buildingId);
}
