package com.ecommerce.webapp.model.dao;

import com.ecommerce.webapp.model.ProductsESIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepo extends ElasticsearchRepository<ProductsESIndex,Integer> {
}