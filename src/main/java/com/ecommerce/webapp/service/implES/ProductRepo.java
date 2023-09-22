package com.ecommerce.webapp.service.implES;

import com.ecommerce.webapp.model.Es_products;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepo extends ElasticsearchRepository<Es_products,Integer> {
}