package com.ecommerce.webapp.service.impl;

import com.ecommerce.webapp.dto.ProductDTO;
import com.ecommerce.webapp.exception.CategoryNotFoundException;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.model.ProductCategory;
import com.ecommerce.webapp.repository.ProductCategoryRepository;
import com.ecommerce.webapp.repository.ProductDAO;
import com.ecommerce.webapp.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


import java.util.stream.Collectors;

/**
 * Service for handling product actions.
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    public static final String CATEGORY_NOT_FOUND = "Could not find provided category : ";
    public static final String PRODUCT_NOT_FOUND = "Product not found with ID: ";
    private ProductDAO productDAO;


    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public ProductServiceImpl(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * Gets the all products available.
     *
     * @return The list of products.
     */
    public List<Product> getProducts() {

        return productDAO.findAll();
    }

    public List<String> getAllProductCategories() {

        return productCategoryRepository.findAll().stream()
                .map(ProductCategory::getCategoryName)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a list of product DTOs based on the specified category.
     *
     * @param category The category for which products are to be retrieved.
     * @return A list of ProductDTOs containing product information.
     */
    public List<ProductDTO> getProductsByCategory(String category) {
        List<Product> products = productDAO.findByCategoryCategoryName(category);
        try {
            return products.stream()
                    .map(product -> {
                        ProductDTO dto = new ProductDTO();
                        dto.setId(product.getId());
                        dto.setProductName(product.getName());
                        dto.setShortDescription(product.getShortDescription());
                        dto.setPrice(product.getPrice());
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new CategoryNotFoundException(CATEGORY_NOT_FOUND + category);
        }
    }

    public List<String> autocompleteProductSearch(String keyword) {
        return productDAO.findByNameContainingIgnoreCase(keyword)
                .stream()
                .map(Product::getName)
                .collect(Collectors.toList());
    }

    /**
     * Get a product by its ID.
     *
     * @param productId The ID of the product to retrieve.
     * @return The product with the specified ID.
     */
    public Product getProductById(Long productId) {
        return productDAO.findById(productId)
                .orElseThrow(() -> new RuntimeException(PRODUCT_NOT_FOUND + productId));
    }

}
