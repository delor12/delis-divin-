package com.delisdivin.dto;

import com.delisdivin.entity.DeliveryStatus;
import lombok.Data;

@Data
public class DeliveryDTO {
    private Long id;
    private Long orderId;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private DeliveryStatus status;
    private Double gpsLatitude;
    private Double gpsLongitude;
}
