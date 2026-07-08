package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "plan_name", nullable = false)
    private String planName; // e.g., Basic, Premium, Enterprise

    @Column(nullable = false)
    private Double price; // Price in FCFA

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private String status; // ACTIVE, EXPIRED, CANCELLED
}
