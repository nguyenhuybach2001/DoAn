package com.bach.RoomRentalManagementSystem.model;

public enum ServiceBillName {
    RENT("Tiền nhà"),
    ELECTRIC("Dịch vụ điện"),
    WATER("Dịch vụ nước"),
    CLEANING("Dịch vụ vệ sinh"),
    INTERNET("Dịch vụ internet"),
    PARKING("Dịch vụ đỗ xe"),
    OTHERS("Dịch vụ khác"),
    ELECTRICITY_AND_WATER("Dịch vụ điện, nước");

    private final String vietnameseName;

    ServiceBillName(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}

