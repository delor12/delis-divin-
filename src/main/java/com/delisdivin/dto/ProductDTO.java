package com.delisdivin.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private Long restaurantId;
    private Long categoryId;
    private String categoryName;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private boolean available;
    private boolean beverage;
    private boolean dessert;
    private Integer stockQuantity;
}
