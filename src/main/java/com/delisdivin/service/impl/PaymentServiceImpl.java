package com.delisdivin.service.impl;

import com.delisdivin.dto.BillDTO;
import com.delisdivin.dto.OrderDTO;
import com.delisdivin.dto.PaymentDTO;
import com.delisdivin.entity.*;
import com.delisdivin.exception.BadRequestException;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.BillRepository;
import com.delisdivin.repository.OrderRepository;
import com.delisdivin.repository.PaymentRepository;
import com.delisdivin.repository.RestaurantRepository;
import com.delisdivin.service.PaymentService;
import com.delisdivin.utils.PdfGenerator;
import com.delisdivin.repository.DiningTableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final DiningTableRepository tableRepository;
    private final AppMapper mapper;
    private final PdfGenerator pdfGenerator;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public PaymentDTO processPayment(PaymentDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + dto.getOrderId()));

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + dto.getRestaurantId()));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setRestaurant(restaurant);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.SUCCESS); // Assume payment succeeds for simulation
        payment.setTransactionReference(dto.getTransactionReference() != null ? 
                dto.getTransactionReference() : "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Payment saved = paymentRepository.save(payment);

        // Mark the order as paid
        order.setPaid(true);

        // If the order has already been served, mark it as COMPLETED and release the table if applicable
        if (order.getStatus() == OrderStatus.SERVED) {
            order.setStatus(OrderStatus.COMPLETED);
            if (order.getTable() != null) {
                DiningTable table = order.getTable();
                table.setStatus(TableStatus.FREE);
                tableRepository.save(table);
            }
        }
        orderRepository.save(order);

        // Notify dashboards via WebSocket
        try {
            OrderDTO orderDto = mapper.toDto(order);
            messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId() + "/orders", orderDto);
            messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId() + "/kitchen", orderDto);
        } catch (Exception e) {
            log.error("Failed to send WebSocket update: {}", e.getMessage());
        }

        // Generate Bill invoice automatically
        generateInvoice(order.getId());

        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found with ID: " + id));
        return mapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByRestaurant(Long restaurantId) {
        return paymentRepository.findByRestaurantId(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BillDTO generateInvoice(Long orderId) {
        // Return existing bill if already generated
        if (billRepository.findByOrderId(orderId).isPresent()) {
            return mapper.toDto(billRepository.findByOrderId(orderId).get());
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        double total = order.getTotalAmount();
        double taxRate = 0.18; // 18% standard VAT
        double subtotal = total / (1 + taxRate);
        double taxAmount = total - subtotal;

        Bill bill = new Bill();
        bill.setOrder(order);
        bill.setRestaurant(order.getRestaurant());
        bill.setBillNumber("INV-" + System.currentTimeMillis() + "-" + order.getId());
        bill.setSubTotal(subtotal);
        bill.setTaxAmount(taxAmount);
        bill.setTotalAmount(total);
        bill.setIssuedAt(LocalDateTime.now());

        // Generate PDF bytes
        byte[] pdfBytes = pdfGenerator.generateInvoicePdf(bill);

        // Write PDF to disk
        String filename = "invoice_" + bill.getBillNumber() + ".pdf";
        File dir = new File(uploadDir + "/invoices");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(pdfBytes);
            bill.setPdfPath(file.getAbsolutePath());
            log.info("Invoice PDF saved successfully at: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to write invoice PDF file: {}", e.getMessage());
            bill.setPdfPath(null); // Fallback: save bill record without path
        }

        Bill saved = billRepository.save(bill);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillDTO getBillByOrderId(Long orderId) {
        Bill bill = billRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found for order ID: " + orderId));
        return mapper.toDto(bill);
    }
}
