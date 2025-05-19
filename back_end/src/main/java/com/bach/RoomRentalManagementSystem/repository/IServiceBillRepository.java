package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.ServiceBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface IServiceBillRepository extends JpaRepository<ServiceBill, Long>, JpaSpecificationExecutor<ServiceBill> {
    Page<ServiceBill> findAll(Specification<ServiceBill> spec, Pageable pageable);

//    @Query("SELECT sb FROM ServiceBill sb WHERE sb.date BETWEEN :startDate AND :endDate AND sb.status = 'PAID'")
//    List<ServiceBill> findByDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    //List<ServiceBill> findByRoomIdAndDateBetween(Long roomId, Date startDate, Date endDate);
}
