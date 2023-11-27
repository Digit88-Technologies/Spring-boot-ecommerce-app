package com.ecommerce.webapp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ecommerce.webapp.model.ProductsESIndex;
import com.ecommerce.webapp.util.ESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ESService {

   @Autowired
   private ElasticsearchClient  elasticsearchClient;


    public SearchResponse<ProductsESIndex> autoSuggestProduct(String partialProductName) throws IOException {

        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(partialProductName);
       SearchResponse<ProductsESIndex> searchResponse  = elasticsearchClient
                .search(s->s.index("products").query(supplier.get()), ProductsESIndex.class);
        System.out.println(" elasticsearch auto suggestion query"+supplier.get().toString());
        return searchResponse;
    }

}