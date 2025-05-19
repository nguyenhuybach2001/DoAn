package com.bach.RoomRentalManagementSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "room_number", nullable = false)
    private String roomNumber;

    @Column(name = "acreage")
    private Long acreage;

    @Column(name = "price")
    private Double price;

    @Column(name = "status", nullable = false)
    private RoomStatus status;

    @Column(name = "room_type")
    private String roomType;

    @Column(name = "floor")
    private Integer floor;

    @Column(name = "max_occupants")
    private Integer maxOccupants;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", referencedColumnName = "building_id", nullable = false)
    private Building building;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<RentalContract> rentalContracts;

    @ManyToOne
    @JoinColumn(name = "management_id")
    private User staff;
}
