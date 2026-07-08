package com.delisdivin.service;

import com.delisdivin.dto.DeliveryDTO;
import com.delisdivin.entity.DeliveryStatus;
import java.util.List;

public interface DeliveryService {
    DeliveryDTO createDelivery(Long orderId);
    DeliveryDTO updateDeliveryStatus(Long id, DeliveryStatus status);
    DeliveryDTO updateGpsLocation(Long id, Double latitude, Double longitude);
    DeliveryDTO getDeliveryById(Long id);
    List<DeliveryDTO> getDeliveriesByDeliverer(Long delivererId);
    List<DeliveryDTO> getActiveDeliveries();
}
