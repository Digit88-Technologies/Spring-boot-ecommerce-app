package com.ecommerce.webapp.model.dao;

import com.ecommerce.webapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Data Access Object for accessing Product data.
 */
public interface ProductDAO extends JpaRepository<Product, Long> {

    List<Product> findByCategoryCategoryName(String categoryName);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query("SELECT DISTINCT p.category.categoryName FROM Product p")
    List<String> findAllCategories();

    @Query("select distinct p from Product p where p.id = :id")
    List<Product> findDistinctById(@Param("id") Long id);


}
