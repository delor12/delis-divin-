package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class Restaurant extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private Double rating = 5.0;

    @Column(name = "price_range", nullable = false)
    private String priceRange = "Medium"; // Low, Medium, High

    @Column(name = "average_prep_time", nullable = false)
    private Integer averagePrepTime = 25; // in minutes

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "latitude")
    private Double latitude = 3.8480;

    @Column(name = "longitude")
    private Double longitude = 11.5021;

    @Column(nullable = false)
    private boolean active = true;
}
