package com.bach.RoomRentalManagementSystem.repository;

import com.bach.RoomRentalManagementSystem.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IVehicleRepository extends JpaRepository<Vehicle, Long> {
    Page<Vehicle> findAll(Specification<Vehicle> spec, Pageable pageable);
}
