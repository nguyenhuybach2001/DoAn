package com.bach.RoomRentalManagementSystem.model;

public enum RoomUtilityType {
    ELECTRICITY("Dịch vụ điện"),
    WATER("Dịch vụ nước");

    private final String vietnameseName;

    RoomUtilityType(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
