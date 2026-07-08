package com.delisdivin.service.impl;

import com.delisdivin.dto.DeliveryDTO;
import com.delisdivin.entity.*;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.DeliveryRepository;
import com.delisdivin.repository.OrderRepository;
import com.delisdivin.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final AppMapper mapper;

    @Override
    @Transactional
    public DeliveryDTO createDelivery(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        Delivery delivery = new Delivery();
        delivery.setOrder(order);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        
        Delivery saved = deliveryRepository.save(delivery);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public DeliveryDTO updateDeliveryStatus(Long id, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery record not found with ID: " + id));

        delivery.setStatus(status);

        // Map delivery status to order status if complete
        if (status == DeliveryStatus.DELIVERED) {
            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        } else if (status == DeliveryStatus.CANCELLED) {
            Order order = delivery.getOrder();
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        }

        Delivery saved = deliveryRepository.save(delivery);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public DeliveryDTO updateGpsLocation(Long id, Double latitude, Double longitude) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery record not found with ID: " + id));

        delivery.setGpsLatitude(latitude);
        delivery.setGpsLongitude(longitude);

        Delivery saved = deliveryRepository.save(delivery);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDTO getDeliveryById(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery record not found with ID: " + id));
        return mapper.toDto(delivery);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDTO> getDeliveriesByDeliverer(Long delivererId) {
        return deliveryRepository.findByDeliveryPersonId(delivererId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryDTO> getActiveDeliveries() {
        return deliveryRepository.findByStatus(DeliveryStatus.ASSIGNED).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}
