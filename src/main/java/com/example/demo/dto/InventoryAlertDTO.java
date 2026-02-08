package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryAlertDTO {
    private String ingredientName;
    private int currentStock;
    private double dailyBurnRate; //average usage per day
    private int daysUntilStockout;
    private String status; //'ok', 'warning', 'critical'
    private String recommendation; //'order now', 'wait'

    private double suggestedPriceMarkup; //'0.15' for 15%
    private String pricingRationale; //'high demand', 'low stock'

    private String dataSource;
}
