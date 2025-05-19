package com.bach.RoomRentalManagementSystem.service;


import com.bach.RoomRentalManagementSystem.dto.BuildingDto;
import com.bach.RoomRentalManagementSystem.dto.UserDto;
import com.bach.RoomRentalManagementSystem.model.Building;
import com.bach.RoomRentalManagementSystem.model.User;
import com.bach.RoomRentalManagementSystem.repository.IBuildingRepository;
import com.bach.RoomRentalManagementSystem.repository.IUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BuildingService {

    private final IBuildingRepository iBuildingRepository;
    private final IUserRepository iUserRepository;

    public Map<String, String> createBuildingService(BuildingDto buildingDto) {
        Map<String, String> response = new HashMap<>();

        Building building = mapToBuilding(buildingDto);

        iBuildingRepository.save(building);

        response.put("message", "Building created successfully");
        response.put("status", "success");

        return response;
    }

    public Map<String, String> updateBuildingService(BuildingDto buildingDto) {
        Map<String, String> response = new HashMap<>();

        Building building = iBuildingRepository.findById(buildingDto.getId())
                .orElseThrow(() -> new RuntimeException("Building not found"));

        mapToBuilding(buildingDto, building);

        iBuildingRepository.save(building);

        response.put("message", "Building updated successfully");
        response.put("status", "success");

        return response;
    }

//    public Map<String, String> setBuildingManager(Long building_id, String email) {
//        Map<String, String> response = new HashMap<>();
//        User staff = iUserRepository.findByEmail(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        Building building = iBuildingRepository.findById(building_id)
//                .orElseThrow(() -> new RuntimeException("Building not found"));
//
//        building.getStaffs().add(staff);
//
//        iBuildingRepository.save(building);
//        response.put("message", "Building updated successfully");
//        response.put("status", "success");
//
//        return response;
//    }

    public Map<String, String> deleteBuildingService(Long building_id) {
        Map<String, String> response = new HashMap<>();

        Building building = iBuildingRepository.findById(building_id)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        building.setIsActive(false);

        iBuildingRepository.save(building);

        response.put("message", "Building deleted successfully");
        response.put("status", "success");

        return response;
    }public Map<String, String> showBuildingService(Long building_id) {
        Map<String, String> response = new HashMap<>();

        Building building = iBuildingRepository.findById(building_id)
                .orElseThrow(() -> new RuntimeException("Building not found"));

        building.setIsActive(true);

        iBuildingRepository.save(building);

        response.put("message", "Building deleted successfully");
        response.put("status", "success");

        return response;
    }

    public List<BuildingDto> getListOfBuildingByAdmin() {
        List<BuildingDto> buildingDtos = new ArrayList<>();

            List<Building> buildings = iBuildingRepository.findAll();
            for (Building building : buildings) {

                BuildingDto dto = mapToDto(building);

                buildingDtos.add(dto);
            }
            return buildingDtos;
        }

//    public List<BuildingDto> getListOfBuildingByStaff(Long staff_id) {
//        List<BuildingDto> buildingDtos = new ArrayList<>();
//        List<Building> buildings = iBuildingRepository.findByIsActiveTrueAndStaffs_Id(staff_id);
//
//        for (Building building : buildings) {
//
//            BuildingDto dto = mapToDto(building);
//
//            buildingDtos.add(dto);
//        }
//        return buildingDtos;
//    }

    private Building mapToBuilding(BuildingDto buildingDto) {
        Building building = new Building();
        building.setName(buildingDto.getName());
        building.setAddress(buildingDto.getAddress());
        building.setImage(buildingDto.getImage());
        building.setTotalRooms(buildingDto.getTotalRoom());
        return building;
    }

    private void mapToBuilding(BuildingDto buildingDto, Building building) {
        building.setName(buildingDto.getName());
        building.setAddress(buildingDto.getAddress());
        building.setImage(buildingDto.getImage());
        building.setTotalRooms(buildingDto.getTotalRoom());
        building.setIsActive(buildingDto.getIsActive());
    }

    private BuildingDto mapToDto(Building building) {
        return BuildingDto.builder()
                .id(building.getBuildingId())
                .name(building.getName())
                .address(building.getAddress())
                .totalRoom(building.getTotalRooms())
                .image(building.getImage())
                .isActive(building.getIsActive())
                .build();
    }

}
