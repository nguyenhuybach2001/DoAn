package com.bach.RoomRentalManagementSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.RoleName;

public interface IRoleRepository extends JpaRepository<Role, Long>{

	Role findByRoleName(RoleName roleName);
	
}
