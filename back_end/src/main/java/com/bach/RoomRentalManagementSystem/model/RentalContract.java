package com.bach.RoomRentalManagementSystem.model;

import com.fasterxml.jackson.databind.node.DoubleNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.sql.Date;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rental_contracts")
public class RentalContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "rent_price")
    private Double rentPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id", referencedColumnName = "room_id", nullable = false)
    private Room room;

    @Column(name = "pdf_file")
    private String pdfFile;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "user_id")
    private User customer;
}
