package com.gobag.gobag_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerNumber;
    private String restaurantName;
    private String deliveryAddress;

    @ElementCollection
    private List<String> items; // List of ordered food items

    private Double totalAmount;
    private String paymentStatus; // PENDING, PAID, FAILED
    private String orderStatus;   // PLACED, CONFIRMED, OUT_FOR_DELIVERY, DELIVERED

    private LocalDateTime orderTime;
}
