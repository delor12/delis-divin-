package com.delisdivin.dto;

import com.delisdivin.entity.PaymentMethod;
import com.delisdivin.entity.PaymentStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private Long restaurantId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private LocalDateTime createdAt;
}
