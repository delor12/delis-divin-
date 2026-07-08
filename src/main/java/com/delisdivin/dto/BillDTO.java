package com.delisdivin.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BillDTO {
    private Long id;
    private Long orderId;
    private Long restaurantId;
    private String billNumber;
    private Double subTotal;
    private Double taxAmount;
    private Double totalAmount;
    private LocalDateTime issuedAt;
    private String pdfPath;
}
