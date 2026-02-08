package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ingredients")
public class Ingredient {

    @Id
    @Column(name = "ing_id")
    private Long id;

    private String name;

    @Column(name = "unit_cost")
    private Double unitCost;

    @Column(name = "lead_time_days")
    private int leadTimeDays;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;
}
