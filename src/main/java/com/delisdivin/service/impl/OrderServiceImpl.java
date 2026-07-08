package com.delisdivin.service.impl;

import com.delisdivin.dto.OrderDTO;
import com.delisdivin.dto.OrderItemDTO;
import com.delisdivin.entity.*;
import com.delisdivin.exception.BadRequestException;
import com.delisdivin.exception.ResourceNotFoundException;
import com.delisdivin.mapper.AppMapper;
import com.delisdivin.repository.*;
import com.delisdivin.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DiningTableRepository tableRepository;
    private final AppUserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AppMapper mapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with ID: " + dto.getRestaurantId()));

        Order order = new Order();
        order.setRestaurant(restaurant);
        order.setClientName(dto.getClientName());
        order.setClientPhone(dto.getClientPhone());
        order.setClientAddress(dto.getClientAddress());
        order.setType(dto.getType());
        order.setStatus(OrderStatus.PENDING);

        if (dto.getTableId() != null) {
            DiningTable table = tableRepository.findById(dto.getTableId())
                    .orElseThrow(() -> new ResourceNotFoundException("Table not found with ID: " + dto.getTableId()));
            order.setTable(table);
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        if (dto.getWaiterId() != null) {
            AppUser waiter = userRepository.findById(dto.getWaiterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Waiter not found with ID: " + dto.getWaiterId()));
            order.setWaiter(waiter);
        }

        double totalAmount = 0.0;

        for (OrderItemDTO itemDto : dto.getOrderItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + itemDto.getProductId()));

            if (!product.isAvailable()) {
                throw new BadRequestException("Product " + product.getName() + " is currently unavailable.");
            }

            if (product.getStockQuantity() < itemDto.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName() + ". Available: " + product.getStockQuantity());
            }

            // Deduct stock
            product.setStockQuantity(product.getStockQuantity() - itemDto.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setSpecialNotes(itemDto.getSpecialNotes());

            order.addOrderItem(orderItem);
            totalAmount += product.getPrice() * itemDto.getQuantity();
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);

        OrderDTO responseDto = mapper.toDto(savedOrder);

        // Notify via WebSocket
        sendWebSocketUpdate(dto.getRestaurantId(), "NEW_ORDER", responseDto);

        return responseDto;
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        // If completed or cancelled, release dining table if applicable
        if ((status == OrderStatus.COMPLETED || status == OrderStatus.CANCELLED) && order.getTable() != null) {
            DiningTable table = order.getTable();
            table.setStatus(TableStatus.FREE);
            tableRepository.save(table);
        }

        // If cancelled, restore product stocks
        if (status == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        Order saved = orderRepository.save(order);
        OrderDTO responseDto = mapper.toDto(saved);

        // Notify via WebSocket
        sendWebSocketUpdate(order.getRestaurant().getId(), "STATUS_CHANGE", responseDto);

        // Specific notification for waiter when food is ready
        if (status == OrderStatus.READY) {
            messagingTemplate.convertAndSend("/topic/restaurant/" + order.getRestaurant().getId() + "/waiter", 
                "Order #" + order.getId() + " is ready for table " + (order.getTable() != null ? order.getTable().getNumber() : "Takeout"));
        }

        return responseDto;
    }

    @Override
    @Transactional
    public OrderDTO assignWaiter(Long id, Long waiterId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        
        AppUser waiter = userRepository.findById(waiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Waiter not found with ID: " + waiterId));

        order.setWaiter(waiter);
        Order saved = orderRepository.save(order);
        
        OrderDTO responseDto = mapper.toDto(saved);
        sendWebSocketUpdate(order.getRestaurant().getId(), "WAITER_ASSIGNED", responseDto);
        return responseDto;
    }

    @Override
    @Transactional
    public OrderDTO assignDeliveryPerson(Long id, Long deliveryPersonId) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        
        AppUser deliveryPerson = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery person not found with ID: " + deliveryPersonId));

        order.setDeliveryPerson(deliveryPerson);
        Order saved = orderRepository.save(order);
        
        OrderDTO responseDto = mapper.toDto(saved);
        sendWebSocketUpdate(order.getRestaurant().getId(), "DELIVERY_ASSIGNED", responseDto);
        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + id));
        return mapper.toDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByRestaurant(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getActiveOrdersByRestaurant(Long restaurantId) {
        // Active orders: Pending, Preparing, Ready, Served
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.PENDING, OrderStatus.PREPARING, OrderStatus.READY, OrderStatus.SERVED
        );
        return orderRepository.findByRestaurantIdAndStatusIn(restaurantId, activeStatuses).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByWaiter(Long waiterId, OrderStatus status) {
        return orderRepository.findByWaiterIdAndStatus(waiterId, status).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByDeliveryPerson(Long deliveryPersonId) {
        return orderRepository.findByDeliveryPersonId(deliveryPersonId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countOrdersByStatus(Long restaurantId, OrderStatus status) {
        return orderRepository.countByRestaurantIdAndStatus(restaurantId, status);
    }

    private void sendWebSocketUpdate(Long restaurantId, String type, OrderDTO order) {
        try {
            messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId + "/orders", order);
            messagingTemplate.convertAndSend("/topic/restaurant/" + restaurantId + "/kitchen", order);
        } catch (Exception e) {
            log.error("Failed to send WebSocket update: {}", e.getMessage());
        }
    }
}
