package com.bach.RoomRentalManagementSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Data
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "room_utility")
public class RoomUtility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "utility_id")
    private Long utility_id;

    private Date date;

    private Double usage_electricity;

    private Double usage_water;

//    @Enumerated(EnumType.STRING)
//    private RoomUtilityType utilityType;

    @ManyToOne(fetch = FetchType.EAGER)
    private Room room;

}
