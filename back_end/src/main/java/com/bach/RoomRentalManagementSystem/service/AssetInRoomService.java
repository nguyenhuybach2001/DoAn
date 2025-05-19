package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.AssetInRoomDto;
import com.bach.RoomRentalManagementSystem.model.AssetInRoom;
import com.bach.RoomRentalManagementSystem.model.AssetStatus;
import com.bach.RoomRentalManagementSystem.model.Room;
import com.bach.RoomRentalManagementSystem.repository.IAssetInRoomRepository;
import com.bach.RoomRentalManagementSystem.repository.IRoomRepository;
import jakarta.persistence.criteria.Predicate;
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
public class AssetInRoomService {

    private final IAssetInRoomRepository assetInRoomRepository;
    private final IRoomRepository roomRepository;

//    public Map<String, String> createAssetInRoomService(AssetInRoomDto assetInRoomDto) {
//        Map<String, String> response = new HashMap<>();
//        AssetInRoom asset = new AssetInRoom();
//
//        mapToAssetInRoom(assetInRoomDto, asset);
//
//        assetInRoomRepository.save(asset);
//
//        response.put("message", "Asset created successfully");
//        response.put("status", "success");
//        return response;
//    }

        public Map<String, String> createAssetsInRoomService(List<AssetInRoomDto> assetInRoomDtoList) {
        Map<String, String> response = new HashMap<>();
        List<AssetInRoom> assets = new ArrayList<>();

        for (AssetInRoomDto assetInRoomDto : assetInRoomDtoList) {
            AssetInRoom asset = new AssetInRoom();
            mapToAssetInRoom(assetInRoomDto, asset);
            assets.add(asset);
        }

        assetInRoomRepository.saveAll(assets);

        response.put("message", "Assets created successfully");
        response.put("status", "success");
        return response;
    }


    public Map<String, String> updateAssetInRoomService(AssetInRoomDto assetInRoomDto) {
        Map<String, String> response = new HashMap<>();

        AssetInRoom asset = assetInRoomRepository.findById(assetInRoomDto.getId())
                    .orElseThrow(() -> new RuntimeException("Asset not found for ID: " + assetInRoomDto.getId()));

        mapToAssetInRoom(assetInRoomDto, asset);

        assetInRoomRepository.save(asset);


        response.put("message", "Asset created successfully");
        response.put("status", "success");
        return response;
    }

    public AssetInRoomDto getAssetById(Long id) {
        AssetInRoom asset = assetInRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return mapToDto(asset);
    }

    public Map<String, String> deleteAssetsInRoomService(List<Long> ids) {
        Map<String, String> response = new HashMap<>();

        if (ids.isEmpty()) {
            response.put("message", "No IDs provided to delete.");
            response.put("status", "failure");
            return response;
        }
        List<AssetInRoom> assetsToDelete = assetInRoomRepository.findAllById(ids);

        if (assetsToDelete.isEmpty()) {
            response.put("message", "No assets found for the provided IDs.");
            response.put("status", "failure");
            return response;
        }

        assetInRoomRepository.deleteAll(assetsToDelete);
        response.put("message", "Assets deleted successfully");
        response.put("status", "success");
        return response;
    }

    public List<AssetStatus> getAllAssetStatus() {
        return Arrays.asList(AssetStatus.values());
    }

    public Page<AssetInRoomDto> getAssetInRoomFilter(String room_number, Long building_id, String type, String status, Double minPrice, Double maxPrice, Date startInstall, Date endInstall, Date startCheck, Date endCheck, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<AssetInRoom> spec = filterAssetInRoom(room_number, building_id, type, status, minPrice, maxPrice, startInstall, endInstall, startCheck, endCheck);

        Page<AssetInRoom> assetInRoomPage = assetInRoomRepository.findAll(spec, pageable);

        return assetInRoomPage.map(this::mapToDto);
    }

    private Specification<AssetInRoom> filterAssetInRoom(String room_number, Long building_id, String type, String status, Double minPrice, Double maxPrice, Date startInstall, Date endInstall, Date startCheck, Date endCheck) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            predicate = builder.and(predicate, builder.isTrue(root.get("room").get("isActive")));
            predicate = builder.and(predicate, builder.isTrue(root.get("room").get("building").get("isActive")));

            if (room_number != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("roomNumber"), room_number));
            }

            if (building_id != null) {
                predicate = builder.and(predicate, builder.equal(root.get("room").get("building").get("buildingId"), building_id));
            }

            if (type != null && !type.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("type"), "%" + type + "%"));
            }

            if (status != null && !status.isEmpty()) {
                predicate = builder.and(predicate, builder.equal(root.get("status"),  AssetStatus.valueOf(status)));
            }

            if (minPrice != null && maxPrice != null) {
                predicate = builder.and(predicate, builder.between(root.get("price"), minPrice, maxPrice));
            } else if (minPrice != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("price"), minPrice));
            } else if (maxPrice != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (startInstall != null && endInstall != null) {
                predicate = builder.and(predicate, builder.between(root.get("installationDate"), startInstall, endInstall));
            } else if (startInstall != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("installationDate"), startInstall));
            } else if (endInstall != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("installationDate"), endInstall));
            }

            if (startCheck != null && endCheck != null) {
                predicate = builder.and(predicate, builder.between(root.get("lastCheckedDate"), startCheck, endCheck));
            } else if (startCheck != null) {
                predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get("lastCheckedDate"), startCheck));
            } else if (endCheck != null) {
                predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get("lastCheckedDate"), endCheck));
            }

            return predicate;
        };
    }

    private void mapToAssetInRoom(AssetInRoomDto assetInRoomDto, AssetInRoom asset) {
        asset.setType(assetInRoomDto.getType());
        asset.setStatus(assetInRoomDto.getStatus());
        asset.setPrice(assetInRoomDto.getPrice());
        asset.setInstallationDate(assetInRoomDto.getInstallationDate());
        asset.setLastCheckedDate(assetInRoomDto.getLastCheckedDate());
        Room room = roomRepository.findByBuildingBuildingIdAndRoomNumber(assetInRoomDto.getBuilding_id(), assetInRoomDto.getRoom_number())
                .orElseThrow(() -> new RuntimeException("Room not found!"));
        asset.setRoom(room);
    }

    public AssetInRoomDto mapToDto(AssetInRoom assetInRoom) {
        return AssetInRoomDto.builder()
                .id(assetInRoom.getAssetId())
                .type(assetInRoom.getType())
                .status(assetInRoom.getStatus())
                .room_number(assetInRoom.getRoom().getRoomNumber())
                .building_id(assetInRoom.getRoom().getBuilding().getBuildingId())
                .price(assetInRoom.getPrice())
                .installationDate(assetInRoom.getInstallationDate())
                .lastCheckedDate(assetInRoom.getLastCheckedDate())
                .build();
    }

}
