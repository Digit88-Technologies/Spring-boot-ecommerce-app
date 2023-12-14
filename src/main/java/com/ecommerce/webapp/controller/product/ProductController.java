package com.ecommerce.webapp.controller.product;

import com.ecommerce.webapp.dto.ProductDTO;
import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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

        log.info("Fetching all products from inventory");
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

        log.info("Fetching all product categories");
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

        log.info("Fetching products as per requested category");
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    /**
     * Autocomplete product search based on a keyword.
     *
     * @param keyword The keyword to search for.
     * @return The list of product names matching the keyword.
     */
    @GetMapping("/autocomplete")
    public ResponseEntity<List<String>> autocompleteProductSearch(@RequestParam String keyword) {

        log.info("Fetching products as per requested keyword using text auto-suggest");
        List<String> productNames = productService.autocompleteProductSearch(keyword);
        return ResponseEntity.ok(productNames);
    }


}
