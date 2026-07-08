package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
public class Delivery extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_person_id")
    private AppUser deliveryPerson; // Nullable until assigned

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status = DeliveryStatus.ASSIGNED;

    @Column(name = "gps_latitude")
    private Double gpsLatitude = 0.0;

    @Column(name = "gps_longitude")
    private Double gpsLongitude = 0.0;
}
