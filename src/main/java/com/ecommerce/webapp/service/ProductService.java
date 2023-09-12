package com.ecommerce.webapp.service;

import com.ecommerce.webapp.model.Product;
import com.ecommerce.webapp.model.dao.ProductDAO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for handling product actions.
 */
@Service
public class ProductService {

  private ProductDAO productDAO;


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

}
