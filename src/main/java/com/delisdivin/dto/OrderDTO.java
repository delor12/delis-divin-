package com.delisdivin.dto;

import com.delisdivin.entity.OrderStatus;
import com.delisdivin.entity.OrderType;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private Long tableId;
    private Integer tableNumber;
    private String clientName;
    private String clientPhone;
    private String clientAddress;
    private OrderStatus status;
    private OrderType type;
    private Double totalAmount;
    private Long waiterId;
    private String waiterName;
    private Long deliveryPersonId;
    private String deliveryPersonName;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime createdAt;
}
