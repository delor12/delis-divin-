package com.delisdivin.service;

import com.delisdivin.dto.OrderDTO;
import com.delisdivin.entity.OrderStatus;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO updateOrderStatus(Long id, OrderStatus status);
    OrderDTO assignWaiter(Long id, Long waiterId);
    OrderDTO assignDeliveryPerson(Long id, Long deliveryPersonId);
    OrderDTO getOrderById(Long id);
    List<OrderDTO> getOrdersByRestaurant(Long restaurantId);
    List<OrderDTO> getActiveOrdersByRestaurant(Long restaurantId);
    List<OrderDTO> getCookedOrdersToday(Long restaurantId);
    List<OrderDTO> getOrdersByWaiter(Long waiterId, OrderStatus status);
    List<OrderDTO> getOrdersByDeliveryPerson(Long deliveryPersonId);
    long countOrdersByStatus(Long restaurantId, OrderStatus status);
    List<OrderDTO> getAvailableDeliveryOrders(Long deliveryPersonId);
    OrderDTO acceptDeliveryOrder(Long orderId, Long deliveryPersonId);
    void declineDeliveryOrder(Long orderId, Long deliveryPersonId);
}
