package com.bach.RoomRentalManagementSystem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assets_of_building")
public class AssetOfBuilding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long assetId;

    @Column(name="type", nullable = false)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private AssetStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id", referencedColumnName = "building_id", nullable = false)
    private Building building;

    @Column(name = "price")
    private Double price;

    @Column(name = "installation_date")
    private Date installationDate;

    @Column(name = "last_checked_date")
    private Date lastCheckedDate;

}
