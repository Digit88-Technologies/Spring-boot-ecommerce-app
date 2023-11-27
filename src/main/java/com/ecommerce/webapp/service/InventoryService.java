package com.ecommerce.webapp.service;

import com.ecommerce.webapp.model.Inventory;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.model.dao.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Check if there is sufficient inventory for a given product and quantity.
     *
     * @param product  The product to check inventory for.
     * @param quantity The requested quantity.
     * @return True if there is sufficient inventory, false otherwise.
     */
    public boolean hasSufficientInventory(Product product, int quantity) {
        Inventory inventory = inventoryRepository.findByProduct(product);
        return inventory != null && inventory.getQuantity() >= quantity;
    }

    /**
     * Reduce the inventory for a given product by a specified quantity.
     *
     * @param product  The product for which to reduce the inventory.
     * @param quantity The quantity to reduce.
     */
    public void reduceInventory(Product product, int quantity) {
        Inventory inventory = inventoryRepository.findByProduct(product);
        if (inventory != null) {
            int newQuantity = inventory.getQuantity() - quantity;
            inventory.setQuantity(newQuantity);
            inventoryRepository.save(inventory);
        }
    }

    /**
     * Create an inventory entry for a new product.
     *
     * @param product  The product for which to create an inventory entry.
     * @param quantity The initial quantity.
     */
    public void createInventory(Product product, int quantity) {
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }
}
