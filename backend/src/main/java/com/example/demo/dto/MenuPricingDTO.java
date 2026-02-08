package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuPricingDTO {
    private String menuItemName;
    private double currentPrice;
    private double suggestedPrice;
    private String surgeReason;
    private String stockStatus; //"beef is low"
}
