package com.ecommerce.webapp.service;

import com.ecommerce.webapp.model.Product;

/**
 * Service interface for managing inventory.
 */
public interface InventoryService {

    /**
     * Check if there is sufficient inventory for a given product and quantity.
     *
     * @param product  The product to check inventory for.
     * @param quantity The requested quantity.
     * @return True if there is sufficient inventory, false otherwise.
     */
    boolean hasSufficientInventory(Product product, int quantity);

    /**
     * Reduce the inventory for a given product by a specified quantity.
     *
     * @param product  The product for which to reduce the inventory.
     * @param quantity The quantity to reduce.
     */
    void reduceInventory(Product product, int quantity);

    /**
     * Create an inventory entry for a new product.
     *
     * @param product  The product for which to create an inventory entry.
     * @param quantity The initial quantity.
     */
    void createInventory(Product product, int quantity);
}
