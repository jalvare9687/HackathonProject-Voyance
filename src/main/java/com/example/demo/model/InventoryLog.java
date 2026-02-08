package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "inventory_logs")
public class InventoryLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "log_date")
    private LocalDate logDate;

    @Column(name = "location_id")
    private Integer locationId;

    @Column(name = "ing_id")
    private Long ingId;

    @Column(name = "stock_level")
    private Integer stockLevel;

    @Column(name = "reorder_suggested")
    private Boolean reorderSuggested;
}
