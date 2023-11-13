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
    try {
      // Create a new order for the user
      WebOrder order = orderService.createOrder(user);

      // Process each item in the order
      for (OrderItem orderItem : orderData) {
        Long productId = orderItem.getProductId();
        Integer quantity = orderItem.getQuantity();

        // Retrieve the product
        Product product = productService.getProductById(productId);

        // Check if there is enough inventory for the requested quantity
        if (inventoryService.hasSufficientInventory(product, quantity)) {
          // Reduce the inventory
          inventoryService.reduceInventory(product, quantity);

          // Create a new order quantity entry
          WebOrderQuantities orderQuantities = new WebOrderQuantities();
          orderQuantities.setProduct(product);
          orderQuantities.setQuantity(quantity);
          orderQuantities.setOrder(order);

          // Add the order quantity to the order
          order.getQuantities().add(orderQuantities);
        } else {
          // If there is not enough inventory, return an error response
          return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                  .body("Insufficient inventory for product ID: " + productId);
        }
      }

      // Save the order to the database
      orderService.saveOrder(order);

      return ResponseEntity.ok("Order placed successfully!");
    } catch (Exception e) {
      // Handle exceptions and return an error response
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Error placing order: " + e.getMessage());
    }
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
