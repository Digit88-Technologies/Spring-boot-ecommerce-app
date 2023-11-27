package com.ecommerce.webapp.api.controller.order;

import com.ecommerce.webapp.api.model.OrderItem;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.model.WebOrder;
import com.ecommerce.webapp.model.WebOrderQuantities;
import com.ecommerce.webapp.service.InventoryService;
import com.ecommerce.webapp.service.OrderService;
import com.ecommerce.webapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    public static final String INSUFFICIENT_INVENTORY_FOR_PRODUCT_ID = "Insufficient inventory for product ID: ";
    public static final String ORDER_PLACED_SUCCESSFULLY = "Order placed successfully!";
    private OrderService orderService;
    private ProductService productService;
    private InventoryService inventoryService;

    public OrderController(OrderService orderService, ProductService productService, InventoryService inventoryService) {
        this.orderService = orderService;
        this.productService = productService;
        this.inventoryService = inventoryService;
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

        // Create a new order for the user
        WebOrder order = orderService.createOrder(user);

        // Process each item in the order
        for (OrderItem orderItem : orderData) {
            Long productId = orderItem.getProductId();
            Integer quantity = orderItem.getQuantity();

            Product product = productService.getProductById(productId);

            if (inventoryService.hasSufficientInventory(product, quantity)) {
                inventoryService.reduceInventory(product, quantity);

                WebOrderQuantities orderQuantities = new WebOrderQuantities();
                orderQuantities.setProduct(product);
                orderQuantities.setQuantity(quantity);
                orderQuantities.setOrder(order);

                order.getQuantities().add(orderQuantities);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(INSUFFICIENT_INVENTORY_FOR_PRODUCT_ID + productId);
            }
        }

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
        return orderService.getOrders(user);
    }
}
