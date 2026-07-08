package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price; // Price in FCFA

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = false)
    private boolean beverage = false;

    @Column(nullable = false)
    private boolean dessert = false;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;
}
