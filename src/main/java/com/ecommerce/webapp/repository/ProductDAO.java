package com.ecommerce.webapp.repository;

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

    String DISTINCT_PRODUCTS_QUERY = "SELECT DISTINCT p.category.categoryName FROM Product p";
    String PRODUCTS_BY_ID_QUERY = "select distinct p from Product p where p.id = :id";

    List<Product> findByCategoryCategoryName(String categoryName);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    @Query(DISTINCT_PRODUCTS_QUERY)
    List<String> findAllCategories();

    @Query(PRODUCTS_BY_ID_QUERY)
    List<Product> findDistinctById(@Param("id") Long id);


}
