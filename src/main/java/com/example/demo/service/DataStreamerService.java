package com.example.demo.service;

import com.example.demo.model.InventoryLog;
import com.example.demo.model.Order;
import com.example.demo.model.Recipe;
import com.example.demo.repository.InventoryLogRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class DataStreamerService {
    private static final Logger log = LoggerFactory.getLogger(DataStreamerService.class);
    private final Random random = new Random();

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private InventoryLogRepository inventoryLogRepository;
    @Autowired
    private EntityManager entityManager;

    @Scheduled(fixedRate = 5000) //runs every 5 seconds
    @Transactional //ensures all database operations in this method succeed or fail together (no situation where order saves, but inventory fails to update)
    public void generateAndProcessLiveOrder() {
        //pick random location
        int locationId = random.nextInt(5) + 1;
        //decide how many orders to generate
        int ordersToGenerate = random.nextInt(3);
        if (ordersToGenerate == 0) {
            log.info("No new orders");
            return;
        }

        log.info("{} orders for location {}", ordersToGenerate, locationId);

        for (int i = 0; i < ordersToGenerate; i++) {
            //create new order object with random data
            //Order newOrder = new Order();
            /**
            Order newOrder = createRandomOrder(locationId);

            orderRepository.save(newOrder);

            log.info("Order for Menu Item #{} at Location #{}", newOrder.getMenuId(), locationId);

            //deplete inventory based on order recipe
            depleteInventoryForOrder(newOrder);


            /**
            newOrder.setLocationId(locationId);
            newOrder.setOrderDate(LocalDateTime.now());
            newOrder.setCustomerId(random.nextInt(2000) + 1); //random customer
            newOrder.setMenuId(random.nextInt(5) + 1); //random menu item
            newOrder.setQuantity(1);
            newOrder.setTotalPrice(random.nextDouble(5, 20)); //random price $5-$20
            newOrder.setStatus("Completed");
            newOrder.setIsSurgePricing(random.nextBoolean());

            //save new order to database
            orderRepository.save(newOrder);
            **/

            processSingleOrder(locationId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleOrder(int locationId) {
        Order newOrder = createRandomOrder(locationId);
        orderRepository.saveAndFlush(newOrder); //write to db immediately

        depleteInventoryForOrder(newOrder);

        //clear cache so next order starts with fresh slate
        entityManager.flush();
        entityManager.clear();
    }

    private void depleteInventoryForOrder(Order order) {
        //find recipe for menu item
        List<Recipe> recipeItems = recipeRepository.findById_MenuId(order.getMenuId());

        if (recipeItems.isEmpty()) {
            log.warn("No recipe found for Menu ID: {}", order.getMenuId());
            return;
        }

        for (Recipe recipeItem : recipeItems) {
            long ingId = recipeItem.getId().getIngId();
            Integer qtyNeededObj = recipeItem.getQtyNeeded();
            int qtyNeeded = (qtyNeededObj != null) ? qtyNeededObj : 1; // Default to 1 if null

            //find most recent stock level for this ingredient at this location
            Integer currentStock = getCurrentStock(order.getLocationId(), ingId);

            //calculate new stock level
            int newStock = Math.max(0, currentStock - qtyNeeded); //make sure it doesn't go below 0

            //create and save new inventory log entry
            InventoryLog newLog = new InventoryLog();
            newLog.setLogDate(LocalDate.now());
            newLog.setLocationId(order.getLocationId());
            newLog.setIngId(ingId);
            newLog.setStockLevel(newStock);
            newLog.setReorderSuggested(newStock < 20); //reorder

            inventoryLogRepository.saveAndFlush(newLog);
            log.info("Ingredient #{}: Stock at location{} changed from {} to {}", ingId, order.getLocationId(), currentStock, newStock);
        }
    }

    private Integer getCurrentStock(int locationId, long ingId) {
        //same as in DashboardService
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
            //no logs for this item, stock is 0
            return 0;
        }
    }

    private Order createRandomOrder(int locationId) {
        Order order = new Order();
        order.setLocationId(locationId);
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerId(random.nextInt(2000) + 1);
        order.setMenuId(random.nextInt(5) + 1);
        order.setQuantity(1);
        order.setTotalPrice(random.nextDouble(5, 20));
        order.setStatus("Completed");
        order.setIsSurgePricing(random.nextDouble() > 0.8); //20% chance of surge
        return order;
    }
    //TO//DO: draw random variables from non-uniform distributions
    //TO//DO: time-series prediction

    //TODO: implement event scheduling into frontend
    //TODO: implement price changing system into frontend
    //TODO: implement ordering system into frontend (auto restock)
    //TODO: implement map in frontend (location optimization)
    //TODO: implement "create new store at lat,lon" into frontend
    //TODO: implement LLM for querying
}
