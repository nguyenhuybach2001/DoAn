package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.*;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IRentalContractRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserManagementService {

    private final IUserRepository userRepository;
    private final IRentalContractRepository rentalContractRepository;
    private final UserService userService;

    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Room not found with ID: " + userId));
        return mapToDto(user);
    }

    public Page<UserDto> getUserByFilter(String fullName, String email, Date startDate, Date endDate, String identityNumber, String address, String phoneNumber, RoleName role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        UserDto currentUser = userService.getCurrentUser();
        Specification<User> spec = filterUser(fullName, email, startDate, endDate, identityNumber, address, phoneNumber, role, currentUser);

        Page<User> userDtoPage = userRepository.findAll(spec, pageable);

        return userDtoPage.map(this::mapToDto);
    }

//    private Specification<User> filterUser(String fullName, String email, Date startDate, Date endDate, String identityNumber, String address, String phoneNumber, RoleName role, UserDto currentUser) {
//        return (root, query, builder) -> {
//            Predicate predicate = builder.conjunction();
//
//            if (currentUser.getRole().toString().equals("STAFF")) {
//                Join<User, RentalContract> rentalJoin = root.join("rentalContracts", JoinType.INNER);
//                Join<RentalContract, Room> roomJoin = rentalJoin.join("room", JoinType.INNER);
//                predicate = builder.and(builder.equal(roomJoin.get("staff").get("id"), currentUser.getId()));
//            }
//
//            if (fullName != null && !fullName.isEmpty()) {
//                predicate = builder.and(predicate, builder.like(root.get("fullName"), "%" + fullName + "%"));
//            }
//
//            if (email != null && !email.isEmpty()) {
//                predicate = builder.and(predicate, builder.like(root.get("email"), "%" + email + "%"));
//            }
//
//            if (identityNumber != null && !identityNumber.isEmpty()) {
//                predicate = builder.and(predicate, builder.like(root.get("identityNumber"), "%" + identityNumber + "%"));
//            }
//
//            if (address != null && !address.isEmpty()) {
//                predicate = builder.and(predicate, builder.like(root.get("address"), "%" + address + "%"));
//            }
//
//            if (phoneNumber != null && !phoneNumber.isEmpty()) {
//                predicate = builder.and(predicate, builder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
//            }
//
//            if (role != null) {
//                predicate = builder.and(predicate, builder.equal(root.get("role").get("roleName"), role));
//            }
//
//            if (startDate != null && endDate != null) {
//                predicate = builder.and(predicate, builder.between(root.get("dateOfBirth"), startDate, endDate));
//            } else if (startDate != null) {
//                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("dateOfBirth"), startDate));
//            } else if (endDate != null) {
//                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("dateOfBirth"), endDate));
//            }
//
//            return predicate;
//        };
//    }

    private Specification<User> filterUser(String fullName, String email, Date startDate, Date endDate, String identityNumber,
                                           String address, String phoneNumber, RoleName role, UserDto currentUser) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // ✅ Nếu user đang đăng nhập là STAFF mà lại không truyền role=CUSTOMER → chặn lại
            if (currentUser.getRole().toString().equals("STAFF") && role != RoleName.CUSTOMER) {
                // Trả predicate sai để kết quả là rỗng
                return builder.disjunction(); // luôn false
            }

            // ✅ Nếu là STAFF và đang lọc CUSTOMER → chỉ lấy customer của phòng staff đó quản lý
            if (currentUser.getRole().toString().equals("STAFF") && role == RoleName.CUSTOMER) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<RentalContract> contractRoot = subquery.from(RentalContract.class);
                subquery.select(contractRoot.get("customer").get("id"))
                        .where(builder.equal(contractRoot.get("room").get("staff").get("id"), currentUser.getId()));

                predicate = builder.and(predicate, root.get("id").in(subquery));
            }

            // Các điều kiện lọc như bình thường
            if (fullName != null && !fullName.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("fullName"), "%" + fullName + "%"));
            }

            if (email != null && !email.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("email"), "%" + email + "%"));
            }

            if (identityNumber != null && !identityNumber.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("identityNumber"), "%" + identityNumber + "%"));
            }

            if (address != null && !address.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("address"), "%" + address + "%"));
            }

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("phoneNumber"), "%" + phoneNumber + "%"));
            }

            if (role != null) {
                predicate = builder.and(predicate, builder.equal(root.get("role").get("roleName"), role));
            }

            if (startDate != null && endDate != null) {
                predicate = builder.and(predicate, builder.between(root.get("dateOfBirth"), startDate, endDate));
            } else if (startDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("dateOfBirth"), startDate));
            } else if (endDate != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("dateOfBirth"), endDate));
            }

            return predicate;
        };
    }


    private UserDto mapToDto(User user) {
        List<Long> contractsIdList = rentalContractRepository.findContractIdsByUserId(user.getId());

        return UserDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .dateOfBirth(user.getDateOfBirth())
                .identityNumber(user.getIdentityNumber())
                .address(user.getAddress())
                .phoneNumber(user.getPhone())
                .role(RoleName.valueOf(user.getRole().getRoleName()))
                .contracts_id(contractsIdList)
                .email(user.getEmail())
                .build();
    }

}
