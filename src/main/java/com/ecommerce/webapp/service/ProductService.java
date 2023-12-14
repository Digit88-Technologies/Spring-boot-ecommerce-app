package com.ecommerce.webapp.service;

import com.ecommerce.webapp.dto.ProductDTO;
import com.ecommerce.webapp.exception.CategoryNotFoundException;
import com.ecommerce.webapp.model.Product;

import java.util.List;

/**
 * Service interface for handling product actions.
 */
public interface ProductService {

    /**
     * Gets all products available.
     *
     * @return The list of products.
     */
    List<Product> getProducts();

    /**
     * Gets all product categories.
     *
     * @return The list of product categories.
     */
    List<String> getAllProductCategories();

    /**
     * Retrieves a list of product DTOs based on the specified category.
     *
     * @param category The category for which products are to be retrieved.
     * @return A list of ProductDTOs containing product information.
     * @throws CategoryNotFoundException If the specified category is not found.
     */
    List<ProductDTO> getProductsByCategory(String category) throws CategoryNotFoundException;

    /**
     * Autocompletes product search based on the provided keyword.
     *
     * @param keyword The keyword for autocompletion.
     * @return The list of product names matching the keyword.
     */
    List<String> autocompleteProductSearch(String keyword);

    /**
     * Get a product by its ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The product with the specified ID.
     */
    Product getProductById(Long productId);
}
