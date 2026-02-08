package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "menu_id")
    private Integer menuId;

    private Integer quantity;

    @Column(name = "total_price")
    private Double totalPrice;

    private String status;

    @Column(name = "is_surge_pricing")
    private Boolean isSurgePricing;
}
