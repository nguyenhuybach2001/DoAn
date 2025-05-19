package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.MaintenanceExpenses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Date;
import java.util.List;

public interface IMaintenanceExpensesRepository extends JpaRepository<MaintenanceExpenses, Long>, JpaSpecificationExecutor<MaintenanceExpenses> {
    Page<MaintenanceExpenses> findAll(Specification<MaintenanceExpenses> spec, Pageable pageable);

//    @Query("SELECT me FROM MaintenanceExpenses me WHERE me.date BETWEEN :startDate AND :endDate AND me.billStatus = 'PAID'")
//    List<MaintenanceExpenses> findByDateBetween(Date startDate, Date endDate);

    //List<MaintenanceExpenses> findByRoomIdAndDateBetween(Long roomId, Date startDate, Date endDate);

    //List<MaintenanceExpenses> findByBuildingIdAndDateBetween(Long buildingId, Date startDate, Date endDate);
}
