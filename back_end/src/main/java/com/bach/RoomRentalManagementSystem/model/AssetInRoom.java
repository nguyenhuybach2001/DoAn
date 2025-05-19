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
@Table(name = "assets_in_room")
public class AssetInRoom {

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
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    private Room room;

    @Column(name = "price")
    private Double price;

    @Column(name = "installation_date")
    private Date installationDate;

    @Column(name = "last_checked_date")
    private Date lastCheckedDate;

}
