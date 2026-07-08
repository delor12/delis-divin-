package com.delisdivin.service;

import com.delisdivin.dto.BillDTO;
import com.delisdivin.dto.PaymentDTO;
import java.util.List;

public interface PaymentService {
    PaymentDTO processPayment(PaymentDTO paymentDTO);
    PaymentDTO getPaymentById(Long id);
    List<PaymentDTO> getPaymentsByRestaurant(Long restaurantId);
    BillDTO generateInvoice(Long orderId);
    BillDTO getBillByOrderId(Long orderId);
}
