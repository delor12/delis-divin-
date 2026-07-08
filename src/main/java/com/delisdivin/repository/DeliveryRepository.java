package com.delisdivin.repository;

import com.delisdivin.entity.Delivery;
import com.delisdivin.entity.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findByDeliveryPersonId(Long deliveryPersonId);
    List<Delivery> findByStatus(DeliveryStatus status);
}
