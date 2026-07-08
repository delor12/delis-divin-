package com.delisdivin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "bills")
@Getter
@Setter
public class Bill extends AbstractAuditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "bill_number", nullable = false, unique = true)
    private String billNumber;

    @Column(name = "sub_total", nullable = false)
    private Double subTotal;

    @Column(name = "tax_amount", nullable = false)
    private Double taxAmount;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount; // In FCFA

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "pdf_path")
    private String pdfPath;
}
