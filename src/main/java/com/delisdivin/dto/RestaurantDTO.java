package com.delisdivin.dto;

import lombok.Data;

@Data
public class RestaurantDTO {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Long cityId;
    private String cityName;
    private String phone;
    private String email;
    private Double rating;
    private String priceRange;
    private Integer averagePrepTime;
    private String logoUrl;
    private String bannerUrl;
    private Double latitude;
    private Double longitude;
    private boolean active;
}
