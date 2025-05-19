package com.bach.RoomRentalManagementSystem.service;

import com.bach.RoomRentalManagementSystem.dto.AssetInRoomDto;
import com.bach.RoomRentalManagementSystem.dto.AssetOfBuildingDto;
import com.bach.RoomRentalManagementSystem.model.AssetInRoom;
import com.bach.RoomRentalManagementSystem.model.AssetOfBuilding;
import com.bach.RoomRentalManagementSystem.model.AssetStatus;
import com.bach.RoomRentalManagementSystem.model.Building;
import com.bach.RoomRentalManagementSystem.repository.IAssetOfBuildingRepository;
import com.bach.RoomRentalManagementSystem.repository.IBuildingRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AssetOfBuildingService {

    private final IAssetOfBuildingRepository assetOfBuildingRepository;
    private final IBuildingRepository buildingRepository;

//    public Map<String, String> createAssetOfBuildingService(AssetOfBuildingDto assetOfBuildingDto) {
//        Map<String, String> response = new HashMap<>();
//        AssetOfBuilding asset = new AssetOfBuilding();
//
//        mapToAssetOfBuilding(assetOfBuildingDto, asset);
//
//        assetOfBuildingRepository.save(asset);
//
//        response.put("message", "Asset created successfully");
//        response.put("status", "success");
//        return response;
//    }

    public Map<String, String> createAssetsOfBuildingService(List<AssetOfBuildingDto> assetOfBuildingDtoList) {
        Map<String, String> response = new HashMap<>();
        List<AssetOfBuilding> assets = new ArrayList<>();

        for (AssetOfBuildingDto assetOfBuildingDto : assetOfBuildingDtoList) {
            AssetOfBuilding asset = new AssetOfBuilding();
            mapToAssetOfBuilding(assetOfBuildingDto, asset);
            assets.add(asset);
        }

        assetOfBuildingRepository.saveAll(assets);

        response.put("message", "Asset created successfully");
        response.put("status", "success");
        return response;
    }

    public Map<String, String> updateAssetOfBuildingService(AssetOfBuildingDto assetOfBuildingDto) {
        Map<String, String> response = new HashMap<>();
        AssetOfBuilding asset = assetOfBuildingRepository.findById(assetOfBuildingDto.getId())
                .orElseThrow(() -> new RuntimeException("Asset not found!"));

        mapToAssetOfBuilding(assetOfBuildingDto, asset);

        assetOfBuildingRepository.save(asset);

        response.put("message", "Asset updated successfully");
        response.put("status", "success");
        return response;
    }

    public AssetOfBuildingDto getAssetById(Long id) {
        AssetOfBuilding asset = assetOfBuildingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found"));
        return mapToDto(asset);
    }

    public Map<String, String> deleteAssetsOfBuilding(List<Long> assetIds) {
        Map<String, String> response = new HashMap<>();

        if (assetIds.isEmpty()) {
            response.put("message", "No IDs provided to delete.");
            response.put("status", "failure");
            return response;
        }
        List<AssetOfBuilding> assetsToDelete = assetOfBuildingRepository.findAllById(assetIds);

        if (assetsToDelete.isEmpty()) {
            response.put("message", "No assets found for the provided IDs.");
            response.put("status", "failure");
            return response;
        }

        assetOfBuildingRepository.deleteAll(assetsToDelete);

        response.put("message", "Assets deleted successfully");
        response.put("status", "success");
        return response;
    }


    public Page<AssetOfBuildingDto> getAssetOfBuildingFilter(Long building_id, String type, String status, Double minPrice, Double maxPrice, Date startInstall, Date endInstall, Date startCheck, Date endCheck, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<AssetOfBuilding> spec = filterAssetOfBuilding(building_id, type, status, minPrice, maxPrice, startInstall, endInstall, startCheck, endCheck);

        Page<AssetOfBuilding> assetPage = assetOfBuildingRepository.findAll(spec, pageable);

        return assetPage.map(this::mapToDto);
    }

    private Specification<AssetOfBuilding> filterAssetOfBuilding(Long building_id, String type, String status, Double minPrice, Double maxPrice, Date startInstall, Date endInstall, Date startCheck, Date endCheck) {
        return (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            predicate = builder.and(predicate, builder.isTrue(root.get("building").get("isActive")));

            if (building_id != null) {
                predicate = builder.and(predicate, builder.equal(root.get("building").get("buildingId"), building_id));
            }

            if (type != null && !type.isEmpty()) {
                predicate = builder.and(predicate, builder.like(root.get("type"), "%" + type + "%"));
            }

            if (status != null && !status.isEmpty()) {
                predicate = builder.and(predicate, builder.equal(root.get("status"), AssetStatus.valueOf(status)));
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

    private void mapToAssetOfBuilding(AssetOfBuildingDto assetOfBuildingDto, AssetOfBuilding asset) {
        asset.setType(assetOfBuildingDto.getType());
        asset.setStatus(assetOfBuildingDto.getStatus());
        asset.setPrice(assetOfBuildingDto.getPrice());
        asset.setInstallationDate(assetOfBuildingDto.getInstallationDate());
        asset.setLastCheckedDate(assetOfBuildingDto.getLastCheckedDate());

        Building building = buildingRepository.findById(assetOfBuildingDto.getBuilding_id())
                .orElseThrow(() -> new RuntimeException("Building not found!"));
        asset.setBuilding(building);
    }

    public AssetOfBuildingDto mapToDto(AssetOfBuilding assetOfBuilding) {
        return AssetOfBuildingDto.builder()
                .id(assetOfBuilding.getAssetId())
                .type(assetOfBuilding.getType())
                .status(assetOfBuilding.getStatus())
                .price(assetOfBuilding.getPrice())
                .building_id(assetOfBuilding.getBuilding().getBuildingId())
                .installationDate(assetOfBuilding.getInstallationDate())
                .lastCheckedDate(assetOfBuilding.getLastCheckedDate())
                .build();
    }
}
