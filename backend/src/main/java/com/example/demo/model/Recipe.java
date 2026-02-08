package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "recipes")
public class Recipe {
    @EmbeddedId
    private RecipeId id;

    @Column(name = "qty_needed")
    private Integer qtyNeeded;
}
