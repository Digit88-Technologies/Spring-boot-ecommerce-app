package com.ecommerce.webapp.model.dao;

import com.ecommerce.webapp.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Long> {
}
