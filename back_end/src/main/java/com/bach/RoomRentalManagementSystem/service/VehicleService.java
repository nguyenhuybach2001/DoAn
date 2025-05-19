package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.VehicleDto;
import com.bach.RoomRentalManagementSystem.model.Vehicle;
import com.bach.RoomRentalManagementSystem.model.Room;
import com.bach.RoomRentalManagementSystem.model.VehicleType;
import com.bach.RoomRentalManagementSystem.repository.IVehicleRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {

    private final IVehicleRepository vehicleRepository;
    private final IRoomRepository roomRepository;

    public Map<String, String> createVehicle(VehicleDto vehicleDto) {
        Map<String, String> response = new HashMap<>();
        Vehicle vehicle = new Vehicle();

        mapToVehicle(vehicleDto, vehicle);

        vehicleRepository.save(vehicle);

        response.put("message", "Vehicle created successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> createVehicles(List<VehicleDto> vehicleDtos) {
        Map<String, String> response = new HashMap<>();
        List<Vehicle> vehicles = new ArrayList<>();

        for (VehicleDto vehicleDto : vehicleDtos) {
            Vehicle vehicle = new Vehicle();
            mapToVehicle(vehicleDto, vehicle);
            vehicles.add(vehicle);
        }

        vehicleRepository.saveAll(vehicles);

        response.put("message", "Vehicles created successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> updateVehicle(VehicleDto vehicleDto) {
        Map<String, String> response = new HashMap<>();
        Vehicle vehicle = vehicleRepository.findById(vehicleDto.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found!"));

        mapToVehicle(vehicleDto, vehicle);

        vehicleRepository.save(vehicle);

        response.put("message", "Vehicle updated successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> deleteVehicles(List<Long> ids) {
        Map<String, String> response = new HashMap<>();
        if (ids.isEmpty()) {
            response.put("message", "No IDs provided to delete.");
            response.put("status", "failure");
            return response;
        }

        List<Vehicle> vehicles = vehicleRepository.findAllById(ids);

        if (vehicles.isEmpty()) {
            response.put("message", "No vehicles found for the provided IDs.");
            response.put("status", "failure");
            return response;
        }

        vehicleRepository.deleteAll(vehicles);
        response.put("message", "Vehicles deleted successfully");
        response.put("status", "success");
        return response;
    }

    public List<VehicleType> getAllVehicleType() {
        return Arrays.asList(VehicleType.values());
    }

    public Page<VehicleDto> getVehiclesFilter(String room_number, Long building_id, String licensePlate, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Vehicle> spec = filterVehicles(room_number, building_id, licensePlate, type);

        Page<Vehicle> vehiclePage = vehicleRepository.findAll(spec, pageable);

        return vehiclePage.map(this::mapToDto);
    }

    private Specification<Vehicle> filterVehicles(String room_number, Long building_id, String licensePlate, String type) {
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

            if (licensePlate != null && !licensePlate.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("licensePlate"), "%" + licensePlate + "%"));
            }

            if (type != null && !type.isEmpty()) {
                predicate = builder.and(predicate, builder.equal(root.get("type"), VehicleType.valueOf(type)));
            }

            return predicate;
        };
    }

    private void mapToVehicle(VehicleDto vehicleDto, Vehicle vehicle) {
        vehicle.setLicensePlate(vehicleDto.getLicensePlate());
        vehicle.setType(vehicleDto.getType());

        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(vehicleDto.getBuildingId(), vehicleDto.getRoomNumber())
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        vehicle.setRoom(room);
    }

    private VehicleDto mapToDto(Vehicle vehicle) {
        return VehicleDto.builder()
                .id(vehicle.getId())
                .licensePlate(vehicle.getLicensePlate())
                .type(vehicle.getType())
                .roomNumber(vehicle.getRoom().getRoomNumber())
                .buildingId(vehicle.getRoom().getBuilding().getBuildingId())
                .build();
    }
}
