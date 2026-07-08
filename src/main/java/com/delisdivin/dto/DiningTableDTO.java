package com.delisdivin.dto;

import com.delisdivin.entity.TableStatus;
import lombok.Data;

@Data
public class DiningTableDTO {
    private Long id;
    private Long restaurantId;
    private Integer number;
    private Integer capacity;
    private TableStatus status;
    private Double xCoordinate;
    private Double yCoordinate;
}
