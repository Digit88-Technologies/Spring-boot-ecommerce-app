package com.ecommerce.webapp.service.impl;

import com.ecommerce.webapp.model.Inventory;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.repository.InventoryRepository;
import com.ecommerce.webapp.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryServiceImpl(InventoryRepository inventoryRepository) {
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

        log.info("Checking if product has sufficient inventory");
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

        log.info("Reducing Inventory post order placement");
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

        log.info("Creating inventory entry for product " + product.getName());
        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(quantity);
        inventoryRepository.save(inventory);
    }
}
