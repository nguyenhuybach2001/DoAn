package com.bach.RoomRentalManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "building")
public class Building {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "building_id")
    private Long buildingId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "total_rooms")
    private Integer totalRooms;

    @Column(name = "image")
    private String image;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "electricity_price")
    private Double electricityPrice;

    @Column(name = "water_price")
    private Double waterPrice;

}
