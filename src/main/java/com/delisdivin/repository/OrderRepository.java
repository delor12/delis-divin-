package com.delisdivin.repository;

import com.delisdivin.entity.Order;
import com.delisdivin.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByRestaurantId(Long restaurantId);
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
    List<Order> findByRestaurantIdAndStatusIn(Long restaurantId, Collection<OrderStatus> statuses);
    List<Order> findByWaiterIdAndStatus(Long waiterId, OrderStatus status);
    List<Order> findByDeliveryPersonId(Long deliveryPersonId);
    List<Order> findByDeliveryPersonIdAndStatus(Long deliveryPersonId, OrderStatus status);
    List<Order> findByRestaurantIdAndCreatedAtBetween(Long restaurantId, LocalDateTime start, LocalDateTime end);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    long countByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
}
