package com.example.demo.controller;

import com.example.demo.dto.InventoryAlertDTO;
import com.example.demo.dto.MenuPricingDTO;
import com.example.demo.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/pricing-suggestions")
    public List<MenuPricingDTO> getPricing(@RequestParam(defaultValue = "1") int locationId) {
        return dashboardService.getMenuPricingSuggestions(locationId);
    }

    @GetMapping("/stock-alerts")
    public List<InventoryAlertDTO> getStockAlerts(@RequestParam(defaultValue = "1") int locationId) {
        return dashboardService.getManagerDashboard(locationId);
    }
}
