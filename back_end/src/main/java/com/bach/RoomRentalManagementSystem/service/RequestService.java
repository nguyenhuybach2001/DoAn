package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.RequestDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.*;
import com.bach.RoomRentalManagementSystem.repository.IRequestRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoleRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestService {

    private final IRequestRepository requestRepository;
    private final IRoomRepository roomRepository;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public Map<String, String> createRequestService(RequestDto requestDto) {
        Map<String, String> response = new HashMap<>();

        List<User> recipientUserList = new ArrayList<>();
        Request request = new Request();
        User admin = userRepository.findByRole(roleRepository.findByRoleName(RoleName.LANDLORD));
        User staff = requestRepository.findManagerByRoomNumberAndBuildingId(requestDto.getRoom_number(), requestDto.getBuilding_id());
        if (staff != null) {
            recipientUserList.add(staff);
        } else {
            System.out.println("Staff not found for the given room and building.");
        }
        recipientUserList.add(admin);
        UserDto sender = userService.getCurrentUser();

        mapToRequest(requestDto, request);

        requestRepository.save(request);

        for (User user : recipientUserList) {
            notificationService.sendNotification(
                    sender.getId(),
                    user.getId(),
                    "Phòng " + requestDto.getRoom_number() + " có yêu cầu mới cần xác nhận",
                    request.getRqId(),
                    NotiType.REQUEST
            );
        }

        response.put("message", "Request created successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> updateRequestService(RequestDto requestDto) {
        Map<String, String> response = new HashMap<>();
        Request request = requestRepository.findById(requestDto.getId())
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        mapToRequest(requestDto, request);

        requestRepository.save(request);

        response.put("message", "Request updated successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> setRequestStatus(Long id, RequestStatus status) {
        Map<String, String> response = new HashMap<>();

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        request.setStatus(status);
        requestRepository.save(request);

        Map<RequestStatus, String> statusMessages = new HashMap<>();
        statusMessages.put(RequestStatus.IN_PROGRESS, "Yêu cầu của bạn đã được chấp nhận, vui lòng đợi xử lý!");
        statusMessages.put(RequestStatus.COMPLETED, "Yêu cầu của bạn đã được xử lý thành công, vui lòng kiểm tra!");
        statusMessages.put(RequestStatus.CANCELED, "Yêu cầu của bạn đã bị từ chối!");

        String message = statusMessages.get(status);
        if (message == null) {
            throw new RuntimeException("Invalid status provided!");
        }

        UserDto sender = userService.getCurrentUser();
        User receiver = requestRepository.findUserByRequestIdAndStatus(request.getRqId());

        notificationService.sendNotification(
                sender.getId(),
                receiver.getId(),
                message,
                request.getRqId(),
                NotiType.REQUEST
        );

        response.put("message", "Request updated successfully");
        response.put("status", "success");
        return response;
    }


    public RequestDto getRequestById(Long id) {
        Request request = requestRepository.findById(id).orElseThrow(() -> new RuntimeException("Request not found!"));
        return mapToDto(request);
    }

    public Map<String, String> deleteRequest(Long requestId) {
        Map<String, String> response = new HashMap<>();
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found!"));

        requestRepository.delete(request);

        response.put("message", "Request deleted successfully");
        response.put("status", "success");
        return response;
    }


    public List<RequestStatus> getAllRequestStatus() {
        return Arrays.asList(RequestStatus.values());
    }

    public Page<RequestDto> getRequestFilter(String room_number, Long building_id, RequestStatus status, Date startDate, Date endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Request> spec = filterRequest(room_number, building_id, status, startDate, endDate);

        Page<Request> requestPage = requestRepository.findAll(spec, pageable);

        return requestPage.map(this::mapToDto);
    }

    private Specification<Request> filterRequest(String room_number, Long building_id, RequestStatus status, Date startDate, Date endDate) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            predicate = builder.and(predicate, builder.isTrue(root.get("room").get("isActive")));
            predicate = builder.and(predicate, builder.isTrue(root.get("room").get("building").get("isActive")));

            if (room_number != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("room_number"), room_number));
            }

            if (building_id != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("building").get("building_id"), building_id));
            }

            if (status != null) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), status));
            }

            if (startDate != null && endDate != null) {
                predicate = builder.and(predicate, builder.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("start_date"), startDate));
            } else if (endDate != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("end_date"), endDate));
            }

            return predicate;
        };
    }

    private void mapToRequest(RequestDto requestDto, Request request) {

        request.setStartDate(requestDto.getStartDate());
        request.setEndDate(requestDto.getEndDate());
        request.setDescription(requestDto.getDescription());
        request.setNote(requestDto.getNote());

        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(requestDto.getBuilding_id(), requestDto.getRoom_number())
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        request.setRoom(room);
    }

    private RequestDto mapToDto(Request request) {
        return RequestDto.builder()
                .id(request.getRqId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .description(request.getDescription())
                .status(request.getStatus())
                .note(request.getNote())
                .room_number(request.getRoom().getRoomNumber())
                .building_id(request.getRoom().getBuilding().getBuildingId())
                .build();
    }
}
