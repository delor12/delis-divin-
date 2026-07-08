package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dining_tables")
@Getter
@Setter
public class DiningTable extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status = TableStatus.FREE;

    @Column(name = "x_coordinate")
    private Double xCoordinate = 0.0;

    @Column(name = "y_coordinate")
    private Double yCoordinate = 0.0;
}
