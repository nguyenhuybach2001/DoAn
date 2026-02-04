package com.bach.RoomRentalManagementSystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bach.RoomRentalManagementSystem.model.Role;
import com.bach.RoomRentalManagementSystem.model.RoleName;
import com.bach.RoomRentalManagementSystem.model.User;
import com.bach.RoomRentalManagementSystem.repository.IRoleRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import com.bach.RoomRentalManagementSystem.service.UserService;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RoomRentalManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomRentalManagementSystemApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService, IRoleRepository iRoleRepository, IUserRepository iUserRepository,
			PasswordEncoder passwordEncoder,
			com.bach.RoomRentalManagementSystem.repository.IBuildingRepository iBuildingRepository,
			com.bach.RoomRentalManagementSystem.repository.IRoomRepository iRoomRepository) {
		return args -> {
			if (iRoleRepository.count() == 0) {
				userService.saveRole(new Role(RoleName.CUSTOMER));
				userService.saveRole(new Role(RoleName.STAFF));
				userService.saveRole(new Role(RoleName.LANDLORD));
			}

			Role landlordRole = iRoleRepository.findByRoleName(RoleName.LANDLORD);
			Role staffRole = iRoleRepository.findByRoleName(RoleName.STAFF);
			Role customerRole = iRoleRepository.findByRoleName(RoleName.CUSTOMER);

			User adminUser = null;
			if (landlordRole != null) {
				if (!iUserRepository.existsByEmail("admin@gmail.com")) {
					adminUser = new User("admin@gmail.com", passwordEncoder.encode("admin"), landlordRole);
					userService.saverUser(adminUser);
				} else {
					adminUser = iUserRepository.findByEmail("admin@gmail.com").orElse(null);
				}
			}

			if (staffRole != null && !iUserRepository.existsByEmail("staff@gmail.com")) {
				User staffUser = new User("staff@gmail.com", passwordEncoder.encode("staff"), staffRole);
				userService.saverUser(staffUser);
			}

			if (customerRole != null && !iUserRepository.existsByEmail("customer@gmail.com")) {
				User customerUser = new User("customer@gmail.com", passwordEncoder.encode("customer"), customerRole);
				userService.saverUser(customerUser);
			}

			// FAKE DATA BUILDING & ROOMS
			if (iBuildingRepository.count() == 0) {
				com.bach.RoomRentalManagementSystem.model.Building building = new com.bach.RoomRentalManagementSystem.model.Building();
				building.setName("Toa nha A - Green House");
				building.setAddress("123 Duong Lang, Ha Noi");
				building.setTotalRooms(10);
				building.setElectricityPrice(3500.0);
				building.setWaterPrice(25000.0);
				building.setIsActive(true);
				iBuildingRepository.save(building);

				// Create Rooms for Building A
				for (int i = 1; i <= 5; i++) {
					com.bach.RoomRentalManagementSystem.model.Room room = new com.bach.RoomRentalManagementSystem.model.Room();
					room.setRoomNumber("10" + i);
					room.setAcreage(25L);
					room.setPrice(3500000.0);
					room.setStatus(com.bach.RoomRentalManagementSystem.model.RoomStatus.AVAILABLE);
					room.setRoomType("Studio");
					room.setFloor(1);
					room.setMaxOccupants(2);
					room.setDescription("Phong day du tien nghi, thoang mat.");
					room.setBuilding(building);
					room.setIsActive(true);
					if (adminUser != null)
						room.setStaff(adminUser);
					iRoomRepository.save(room);
				}
				for (int i = 1; i <= 5; i++) {
					com.bach.RoomRentalManagementSystem.model.Room room = new com.bach.RoomRentalManagementSystem.model.Room();
					room.setRoomNumber("20" + i);
					room.setAcreage(30L);
					room.setPrice(4500000.0);
					room.setStatus(com.bach.RoomRentalManagementSystem.model.RoomStatus.AVAILABLE);
					room.setRoomType("1 Ngu 1 Khach");
					room.setFloor(2);
					room.setMaxOccupants(3);
					room.setDescription("Phong rong, view dep.");
					room.setBuilding(building);
					room.setIsActive(true);
					if (adminUser != null)
						room.setStaff(adminUser);
					iRoomRepository.save(room);
				}
			}
		};
	}

}
