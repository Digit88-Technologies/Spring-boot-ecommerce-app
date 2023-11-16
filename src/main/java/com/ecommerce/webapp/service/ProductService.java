package com.ecommerce.webapp.service;

import com.ecommerce.webapp.api.model.ProductCategoryDTO;
import com.ecommerce.webapp.api.model.ProductDTO;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.model.ProductCategory;
import com.ecommerce.webapp.model.dao.ProductCategoryRepository;
import com.ecommerce.webapp.model.dao.ProductDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;


import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling product actions.
 */
@Service
public class ProductService {

  private ProductDAO productDAO;


  @Autowired
  private ProductCategoryRepository productCategoryRepository;

  public ProductService(ProductDAO productDAO) {
    this.productDAO = productDAO;
  }

  /**
   * Gets the all products available.
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
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
    }
}
