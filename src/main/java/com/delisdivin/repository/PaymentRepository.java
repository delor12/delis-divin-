package com.delisdivin.repository;

import com.delisdivin.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByRestaurantId(Long restaurantId);
    List<Payment> findByOrderId(Long orderId);
    List<Payment> findByRestaurantIdAndCreatedAtBetween(Long restaurantId, LocalDateTime start, LocalDateTime end);
    List<Payment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
