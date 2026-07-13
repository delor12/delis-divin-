package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private DiningTable table; // Nullable (for takeout or delivery)

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_address")
    private String clientAddress; // Nullable (only for delivery)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType type = OrderType.IN_REST;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0; // In FCFA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "waiter_id")
    private AppUser waiter; // Nullable (for client self-orders or deliveries)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_person_id")
    private AppUser deliveryPerson; // Nullable (only for delivery orders)

    @Column(nullable = false)
    private boolean paid = false;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }
}
