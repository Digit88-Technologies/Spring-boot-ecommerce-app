package com.ecommerce.webapp.controller.order;

import com.ecommerce.webapp.dto.OrderItem;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.model.WebOrder;
import com.ecommerce.webapp.model.WebOrderQuantities;
import com.ecommerce.webapp.service.OrderService;
import com.ecommerce.webapp.service.impl.InventoryServiceImpl;
import com.ecommerce.webapp.service.impl.ProductServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    public static final String INSUFFICIENT_INVENTORY_FOR_PRODUCT_ID = "Insufficient inventory for product ID: ";
    public static final String ORDER_PLACED_SUCCESSFULLY = "Order placed successfully!";
    private OrderService orderService;
    private ProductServiceImpl productServiceImpl;
    private InventoryServiceImpl inventoryServiceImpl;

    public OrderController(OrderService orderService, ProductServiceImpl productServiceImpl, InventoryServiceImpl inventoryServiceImpl) {
        this.orderService = orderService;
        this.productServiceImpl = productServiceImpl;
        this.inventoryServiceImpl = inventoryServiceImpl;
    }

    /**
     * Endpoint to place a new order for the authenticated user.
     *
     * @param user      The user provided by Spring Security context.
     * @param orderData The order data containing product IDs and quantities.
     * @return ResponseEntity with the result of the order placement.
     */
    @PostMapping("/place")
    public ResponseEntity<String> placeOrder(@AuthenticationPrincipal LocalUser user,
                                             @RequestBody List<OrderItem> orderData) {

        log.info("Order placement Request Received");

        // Create a new order for the user
        WebOrder order = orderService.createOrder(user);

        // Process each item in the order
        for (OrderItem orderItem : orderData) {
            Long productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();

            Product product = productServiceImpl.getProductById(productId);

            log.info("Checking the inventory for requested product");
            if (inventoryServiceImpl.hasSufficientInventory(product, quantity)) {
                inventoryServiceImpl.reduceInventory(product, quantity);

                WebOrderQuantities orderQuantities = new WebOrderQuantities();
                orderQuantities.setProduct(product);
                orderQuantities.setQuantity(quantity);
                orderQuantities.setOrder(order);

                order.getQuantities().add(orderQuantities);
            } else {
                log.warn("Insufficient Inventory for requested product : ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(INSUFFICIENT_INVENTORY_FOR_PRODUCT_ID + productId);
            }
        }

        log.info("Creating new order");
        orderService.saveOrder(order);

        return ResponseEntity.ok(ORDER_PLACED_SUCCESSFULLY);

    }

    /**
     * Endpoint to get all orders for a specific user.
     *
     * @param user The user provided by Spring Security context.
     * @return The list of orders the user had made.
     */
    @GetMapping
    public List<WebOrder> getOrders(@AuthenticationPrincipal LocalUser user) {

        log.info("Fetching all the orders for current user");
        return orderService.getOrders(user);
    }
}
