package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @Column(name = "menu_id")
    private Integer id;

    private String name;

    @Column(name = "base_price")
    private Double basePrice;
}
