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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

        // Enforce payment before validating/preparing the order
        if (oldStatus == OrderStatus.PENDING && status != OrderStatus.CANCELLED && status != OrderStatus.PENDING) {
            if (!order.isPaid()) {
                throw new BadRequestException("La commande ne peut être validée qu'après le paiement.");
            }
        }

        // If the order is paid and is now being served, automatically mark it as COMPLETED
        if (status == OrderStatus.SERVED && order.isPaid()) {
            status = OrderStatus.COMPLETED;
        }

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

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getCookedOrdersToday(Long restaurantId) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);
        List<OrderStatus> cookedStatuses = Arrays.asList(OrderStatus.READY, OrderStatus.SERVED, OrderStatus.COMPLETED);
        
        return orderRepository.findByRestaurantIdAndCreatedAtBetween(restaurantId, start, end).stream()
                .filter(o -> cookedStatuses.contains(o.getStatus()))
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAvailableDeliveryOrders(Long deliveryPersonId) {
        AppUser currentDriver = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + deliveryPersonId));

        // Get list of declined order IDs for this driver
        List<Long> declinedIds = Arrays.stream(
                (currentDriver.getDeclinedOrderIds() == null ? "" : currentDriver.getDeclinedOrderIds()).split(",")
        )
        .filter(s -> !s.trim().isEmpty())
        .map(Long::parseLong)
        .collect(Collectors.toList());

        // Find all orders of type DELIVERY and status READY with no assigned delivery person
        List<Order> readyDeliveries = orderRepository.findByStatus(OrderStatus.READY).stream()
                .filter(o -> o.getType() == OrderType.DELIVERY && o.getDeliveryPerson() == null)
                .filter(o -> !declinedIds.contains(o.getId()))
                .collect(Collectors.toList());

        // Find all active drivers with coordinates
        List<AppUser> activeDrivers = userRepository.findByRole(Role.DELIVERY).stream()
                .filter(AppUser::isActive)
                .filter(u -> u.getLatitude() != null && u.getLongitude() != null)
                .collect(Collectors.toList());

        return readyDeliveries.stream().map(order -> {
            OrderDTO dto = mapper.toDto(order);

            // Find closest driver among active drivers who haven't declined this order
            AppUser closestDriver = null;
            double minDistance = Double.MAX_VALUE;

            for (AppUser driver : activeDrivers) {
                // Check if this driver has declined this order
                List<Long> driverDeclinedIds = Arrays.stream(
                        (driver.getDeclinedOrderIds() == null ? "" : driver.getDeclinedOrderIds()).split(",")
                )
                .filter(s -> !s.trim().isEmpty())
                .map(Long::parseLong)
                .collect(Collectors.toList());

                if (driverDeclinedIds.contains(order.getId())) {
                    continue; // Skip driver if they declined
                }

                Restaurant r = order.getRestaurant();
                if (r != null && r.getLatitude() != null && r.getLongitude() != null) {
                    double dist = calculateDistanceInKm(
                            driver.getLatitude(), driver.getLongitude(),
                            r.getLatitude(), r.getLongitude()
                    );
                    if (dist < minDistance) {
                        minDistance = dist;
                        closestDriver = driver;
                    }
                }
            }

            if (closestDriver != null) {
                dto.setProposedDeliveryPersonId(closestDriver.getId());
                dto.setProposedDeliveryPersonName(closestDriver.getFirstName() + " " + closestDriver.getLastName());
            }

            // Also calculate distance from the current driver requesting the list
            Restaurant r = order.getRestaurant();
            if (r != null && r.getLatitude() != null && r.getLongitude() != null &&
                currentDriver.getLatitude() != null && currentDriver.getLongitude() != null) {
                double dist = calculateDistanceInKm(
                        currentDriver.getLatitude(), currentDriver.getLongitude(),
                        r.getLatitude(), r.getLongitude()
                );
                dto.setDistanceToRestaurant(dist);
            } else {
                dto.setDistanceToRestaurant(null);
            }

            return dto;
        }).collect(Collectors.toList());
    }

    private double calculateDistanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double dlon = lon2Rad - lon1Rad;
        double dlat = lat2Rad - lat1Rad;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                 + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                 * Math.pow(Math.sin(dlon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371; // Earth radius in km
        return c * r;
    }

    @Override
    @Transactional
    public OrderDTO acceptDeliveryOrder(Long orderId, Long deliveryPersonId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (order.getDeliveryPerson() != null) {
            throw new BadRequestException("Cette commande a déjà été acceptée par un autre livreur.");
        }

        AppUser driver = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + deliveryPersonId));

        order.setDeliveryPerson(driver);
        Order saved = orderRepository.save(order);

        OrderDTO responseDto = mapper.toDto(saved);
        sendWebSocketUpdate(order.getRestaurant().getId(), "DELIVERY_ACCEPTED", responseDto);
        return responseDto;
    }

    @Override
    @Transactional
    public void declineDeliveryOrder(Long orderId, Long deliveryPersonId) {
        AppUser driver = userRepository.findById(deliveryPersonId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with ID: " + deliveryPersonId));

        String declined = driver.getDeclinedOrderIds();
        if (declined == null || declined.trim().isEmpty()) {
            declined = String.valueOf(orderId);
        } else {
            List<String> list = Arrays.asList(declined.split(","));
            if (!list.contains(String.valueOf(orderId))) {
                declined = declined + "," + orderId;
            }
        }
        driver.setDeclinedOrderIds(declined);
        userRepository.save(driver);

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            sendWebSocketUpdate(order.getRestaurant().getId(), "DELIVERY_DECLINED", mapper.toDto(order));
        }
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
