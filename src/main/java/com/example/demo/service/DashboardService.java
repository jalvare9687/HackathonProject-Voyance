package com.example.demo.service;

import com.example.demo.dto.InventoryAlertDTO;
import com.example.demo.dto.MenuPricingDTO;
import com.example.demo.model.Ingredient;
import com.example.demo.model.Event;
import com.example.demo.model.MenuItem;
import com.example.demo.model.Recipe;
import com.example.demo.repository.IngredientRepository;
import com.example.demo.repository.MenuItemRepository;
import com.example.demo.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public List<MenuPricingDTO> getMenuPricingSuggestions(int locationId) {
        entityManager.clear();
        List<MenuPricingDTO> suggestions = new ArrayList<>();

        //get events in next 48 hours
        String eventSql = "SELECT * FROM events WHERE event_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 1";
        Query eventQuery = entityManager.createNativeQuery(eventSql, Event.class);
        List<Event> events = eventQuery.getResultList();

        //iterate through menu items
        List<MenuItem> menuItems = menuItemRepository.findAll();

        for (MenuItem item : menuItems) {
            double markup = 0.0;
            String reason = "Standard Demand";
            String weakestIngredientName = "";

            //check all ingredients for menu item
            List<Recipe> recipes = recipeRepository.findById_MenuId(item.getId());
            for (Recipe r : recipes) {
                Integer stock = getCurrentStock(locationId, r.getId().getIngId());
                //if stock critically low apply scarcity markup
                if (stock < 15) {
                    markup = Math.max(markup, 0.15);
                    reason = "Inventory Scarcity (Ingredients low)";
                }
            }

            //check for demand surge
            for (Event e : events) {
                if (e.getImpactMultiplier() > 1.3) {
                    markup = Math.max(markup, 0.20);
                    reason = "High Demand Event: " + e.getEventName();
                }
            }

            double suggestedPrice = item.getBasePrice() * (1 + markup);

            suggestions.add(MenuPricingDTO.builder()
                    .menuItemName(item.getName())
                    .currentPrice(item.getBasePrice())
                    .suggestedPrice(Math.round(suggestedPrice * 100.0) / 100.0)
                    .surgeReason(reason)
                    .stockStatus(markup > 0 ? "Surge Pricing Active" : "Stable")
                    .build());
        }
        return suggestions;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public List<InventoryAlertDTO> getManagerDashboard(int locationId) {
        entityManager.clear();
        List<InventoryAlertDTO> alerts = new ArrayList<>();
        List<Ingredient> ingredients = ingredientRepository.findAll();

        //get upcoming events for next 7 days, calculate forecasted demand
        String eventSql = "SELECT * FROM events WHERE event_date BETWEEN CURRENT_DATE AND CURRENT_DATE + 7";
        Query eventQuery = entityManager.createNativeQuery(eventSql, Event.class);
        List<Event> upcomingEvents = eventQuery.getResultList();

        for (Ingredient ing : ingredients) {
            //get the latest stock level
            //Integer currentStock = 0; // Default to 0
            Integer currentStock = getCurrentStock(locationId, ing.getId());
            /**
            try {
                Object result = q.getSingleResult();
                // Check if result is null before casting
                if (result != null) {
                    // Use Number cast to be safe against BigInteger/Integer differences
                    currentStock = ((Number) result).intValue();
                }
            } catch (Exception e) {
                // NoResultException means this ingredient has never been logged at this location
                currentStock = 0;
            }
            **/

            /**
            //identify highest impact event
            double maxMultiplierFound = 1.0;
            String highestImpactEventName = "";
            for (Event e : upcomingEvents) {
                if (e.getImpactMultiplier() > maxMultiplierFound) {
                    maxMultiplierFound = e.getImpactMultiplier();
                    highestImpactEventName = e.getEventName();
                }
            }
            **/
            //calculate burn rate
            /**
            double baseBurnRate = 40.0; //normal daily usage
            double surgeBurnRate = baseBurnRate * maxMultiplierFound;
            int daysUntilStockout = (surgeBurnRate > 0) ? (int) (currentStock / surgeBurnRate) : 999;
            //double totalProjectedUsageNext7Days = 0;
            //String eventAlertNote = "";
            **/
            //calculate historical baseline
            double baseBurnRate = getActualDailyBurnRate(locationId, ing.getId()); //historical average

            //forecast next 7 days
            double totalProjectedUsageNext7Days = 0;
            double maxMultiplierFound = 1.0;
            String highestImpactEventName = "";

            //apply event multipliers
            for (int day = 0; day < 7; day++) {
                LocalDate futureDate = LocalDate.now().plusDays(day);
                double dailyMultiplier = 1.0;

                for (Event e : upcomingEvents) {
                    if (e.getEventDate().equals(futureDate)) {
                        dailyMultiplier = e.getImpactMultiplier();
                        if (dailyMultiplier > maxMultiplierFound) {
                            maxMultiplierFound = dailyMultiplier;
                            highestImpactEventName = e.getEventName();
                        }
                    }
                }
                totalProjectedUsageNext7Days += (baseBurnRate * dailyMultiplier);
            }

            double avgEventAdjustedBurnRate = totalProjectedUsageNext7Days / 7.0;
            int daysUntilStockout = 999;
            if (avgEventAdjustedBurnRate > 0) {
                daysUntilStockout = (int) (currentStock / avgEventAdjustedBurnRate);
            }

            //dynamic pricing
            double suggestedMarkup = 0.0;
            String pricingRationale = "Standard Pricing";

            if (maxMultiplierFound > 1.2) {
                if (daysUntilStockout <= ing.getLeadTimeDays()) {
                    //high demand, low supply (aggressive surge)
                    suggestedMarkup = 0.20;
                    pricingRationale = "Critical Stockout Risk + " + highestImpactEventName;
                } else {
                    //high demand, high stock (moderate surge)
                    suggestedMarkup = 0.10;
                    pricingRationale = "Increased Demand: " + highestImpactEventName;
                }
            } else if (daysUntilStockout <= 1) {
                //no event, low stock (scarcity pricing)
                suggestedMarkup = 0.05;
                pricingRationale = "Inventory Scarcity";
            }
            /**
            //tracking variables
            double maxMultiplierFound = 1.0;
            String highestImpactEventName = "";

            for (int day = 0; day < 7; day++) {
                LocalDate futureDate = LocalDate.now().plusDays(day);
                double dailyMultiplier = 1.0;

                //check if there's an event on that date
                for (Event e: upcomingEvents) {
                    if (e.getEventDate().equals(futureDate)) {
                        dailyMultiplier = e.getImpactMultiplier();

                        //only capture name if this event is most impactful
                        if (dailyMultiplier > maxMultiplierFound) {
                            maxMultiplierFound = dailyMultiplier;
                            highestImpactEventName = e.getEventName();
                        }
                    }
                }
                totalProjectedUsageNext7Days += (baseBurnRate * dailyMultiplier);
            }

            double avgEventAdjustedBurnRate = totalProjectedUsageNext7Days / 7.0;

            String eventAlertNote = highestImpactEventName.isEmpty() ?
                    "" : " [High Impact: " + highestImpactEventName + "]";

            //calculate days until stockout
            int daysUntilStockout = 999; //if burn rate is 0, stock will last "forever"
            if (avgEventAdjustedBurnRate > 0) {
                daysUntilStockout = (int) (currentStock / avgEventAdjustedBurnRate);
            }

            //determine status
            int leadTime = ing.getLeadTimeDays();
            String status = "OK";
            String recommendation = "No action needed";

            if (daysUntilStockout <= leadTime) {
                status = "CRITICAL";
                recommendation = "ORDER IMMEDIATELY: Stockout predicted in " + daysUntilStockout + " days." + eventAlertNote;
            } else if (daysUntilStockout <= leadTime + 2) {
                status = "WARNING";
                recommendation = "Low stock. Reorder within 48 hours." + eventAlertNote;
            }
            **/

            //determine alert status
            //String status = daysUntilStockout <= ing.getLeadTimeDays() ? "CRITICAL" :
            //        (daysUntilStockout <= ing.getLeadTimeDays() + 2 ? "WARNING" : "OK");
            int leadTime = ing.getLeadTimeDays();
            String status = "OK";
            String eventNote = highestImpactEventName.isEmpty() ? "" : " [Impact: " + highestImpactEventName + "]";
            String recommendation = "Healthy Stock" + eventNote;

            if (daysUntilStockout <= leadTime) {
                status = "CRITICAL";
                recommendation = "ORDER IMMEDIATELY: Stockout in " + daysUntilStockout + " days." + eventNote;
            } else if (daysUntilStockout <= leadTime + 2) {
                status = "WARNING";
                recommendation = "Low stock. Reorder within 48 hours." + eventNote;
            }

            //build DTO
            alerts.add(InventoryAlertDTO.builder()
                    .ingredientName(ing.getName())
                    .currentStock(currentStock)
                    .dailyBurnRate(Math.round(avgEventAdjustedBurnRate * 10.0) / 10.0) //round to 1 decimal
                    .daysUntilStockout(daysUntilStockout)
                    .status(status)
                    .suggestedPriceMarkup(suggestedMarkup)
                    .pricingRationale(pricingRationale)
                    .recommendation(status.equals("CRITICAL") ? "ORDER IMMEDIATELY" : "Monitor Stock")
                    .dataSource("aws-0-us-west-2.pooler.supabase.com")
                    //.dataSource("localhost")
                    .build());
        }
        return alerts;
    }

    private Integer getCurrentStock(int locationId, long ingId) {
        String sql = "SELECT stock_level FROM inventory_logs " +
                "WHERE location_id = :locId AND ing_id = :ingId " +
                "ORDER BY log_id DESC LIMIT 1";

        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("locId", locationId);
        q.setParameter("ingId", ingId);

        try {
            Object result = q.getSingleResult();
            return result != null ? ((Number) result).intValue() : 0;
        } catch (Exception e) {
            return 0; //no logs found (e.g. new store)
        }
    }

    private double getActualDailyBurnRate(int locationId, long ingId) {
        // Join orders to recipes on menu_id
        // Sum (order quantity * recipe qty_needed)
        // Filter by location, specific ingredient, and the last 7 days
        String sql = "SELECT COALESCE(SUM(o.quantity * r.qty_needed), 0) / 7.0 " +
                "FROM orders o " +
                "JOIN recipes r ON o.menu_id = r.menu_id " +
                "WHERE o.location_id = :locId " +
                "AND r.ing_id = :ingId " +
                "AND o.order_date >= NOW() - INTERVAL '7 days'";

        Query q = entityManager.createNativeQuery(sql);
        q.setParameter("locId", locationId);
        q.setParameter("ingId", ingId);

        try {
            Object result = q.getSingleResult();
            double actualRate = ((Number) result).doubleValue();

            // If the restaurant is new or has no sales, the rate is 0.
            // We return a minimum "Baseline" of 10.0 so predictions still function.
            return Math.max(actualRate, 10.0);
        } catch (Exception e) {
            return 10.0; // Fallback
        }
    }
}
