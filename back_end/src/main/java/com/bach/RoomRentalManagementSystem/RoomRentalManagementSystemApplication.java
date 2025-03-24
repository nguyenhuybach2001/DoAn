package com.bach.RoomRentalManagementSystem;

import java.util.ArrayList;
import java.util.Optional;

import org.aspectj.weaver.NewConstructorTypeMunger;
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

@SpringBootApplication
public class RoomRentalManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomRentalManagementSystemApplication.class, args);
	}
	
//	@Bean
//    CommandLineRunner run (UserService userService , IRoleRepository iRoleRepository , IUserRepository iUserRepository , PasswordEncoder passwordEncoder)
//    {return args -> {
//        if (iRoleRepository.count() == 0) {
//            userService.saveRole(new Role(RoleName.CUSTOMER));
//            userService.saveRole(new Role(RoleName.STAFF));
//            userService.saveRole(new Role(RoleName.LANDLORD));
//        }
//
//        Role landlordRole = iRoleRepository.findByRoleName(RoleName.LANDLORD);
//        Role staffRole = iRoleRepository.findByRoleName(RoleName.STAFF);
//        Role customerRole = iRoleRepository.findByRoleName(RoleName.CUSTOMER);
//
//        if (landlordRole != null) {
//            User adminUser = new User("admin@gmail.com", passwordEncoder.encode("admin"), landlordRole);
//            userService.saverUser(adminUser);
//        }
//
//        if (staffRole != null) {
//            User staffUser = new User("staff@gmail.com", passwordEncoder.encode("staff"), staffRole);
//            userService.saverUser(staffUser);
//        }
//
//        if (customerRole != null) {
//            User customerUser = new User("customer@gmail.com", passwordEncoder.encode("customer"), customerRole);
//            userService.saverUser(customerUser);
//        }
//    };
//}

}
