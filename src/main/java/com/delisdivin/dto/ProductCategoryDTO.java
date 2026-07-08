package com.delisdivin.dto;

import lombok.Data;

@Data
public class ProductCategoryDTO {
    private Long id;
    private Long restaurantId;
    private String name;
    private String description;
    private boolean active;
    private Integer displayOrder;
}
