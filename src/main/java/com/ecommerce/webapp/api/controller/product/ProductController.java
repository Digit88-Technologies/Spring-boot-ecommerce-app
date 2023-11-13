package com.ecommerce.webapp.api.controller.product;

import com.ecommerce.webapp.api.model.ProductDTO;
import com.ecommerce.webapp.exception.CategoryNotFoundException;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {

  private ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  /**
   * Retrieves the list of products available.
   *
   * @return The list of products.
   */
  @GetMapping
  public ResponseEntity<List<Product>> getProducts() {
    List<Product> products = productService.getProducts();
    return ResponseEntity.ok(products);
  }

  /**
   * Retrieves all product categories.
   *
   * @return The list of product categories.
   */
  @GetMapping("/categories")
  public ResponseEntity<List<String>> getAllCategories() {
    List<String> categories = productService.getAllProductCategories();
    return ResponseEntity.ok(categories);
  }

  /**
   * Searches products by category.
   *
   * @param category The category to search for.
   * @return The list of products in the specified category.
   */
  @GetMapping("/by-category")
  public ResponseEntity<List<ProductDTO>> searchProductsByCategory(@RequestParam String category) {
    try {
      List<ProductDTO> products = productService.getProductsByCategory(category);
      return ResponseEntity.ok(products);
    } catch (CategoryNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }
  }

  /**
   * Autocomplete product search based on a keyword.
   *
   * @param keyword The keyword to search for.
   * @return The list of product names matching the keyword.
   */
  @GetMapping("/autocomplete")
  public ResponseEntity<List<String>> autocompleteProductSearch(@RequestParam String keyword) {
    List<String> productNames = productService.autocompleteProductSearch(keyword);
    return ResponseEntity.ok(productNames);
  }
}
