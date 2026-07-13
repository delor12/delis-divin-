package com.delisdivin.dto;

import com.delisdivin.entity.TableStatus;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DiningTableDTO {
    private Long id;
    private Long restaurantId;
    private Integer number;
    private Integer capacity;
    private TableStatus status;

    @JsonProperty("xCoordinate")
    @JsonAlias("xcoordinate")
    private Double xCoordinate;

    @JsonProperty("yCoordinate")
    @JsonAlias("ycoordinate")
    private Double yCoordinate;
}
