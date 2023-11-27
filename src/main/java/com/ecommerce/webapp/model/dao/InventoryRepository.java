package com.ecommerce.webapp.model.dao;

import com.ecommerce.webapp.model.Inventory;
import com.ecommerce.webapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Inventory findByProduct(Product product);
}
