package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import java.io.Serializable;

@Data
@Embeddable
public class RecipeId implements Serializable {
    @Column(name = "menu_id")
    private Integer menuId;

    @Column(name = "ing_id")
    private Long ingId;
}
