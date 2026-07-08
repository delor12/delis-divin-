package com.delisdivin.dto;

import lombok.Data;

@Data
public class CityDTO {
    private Long id;
    private String name;
    private String postalCode;
    private String country;
    private boolean active;
}
