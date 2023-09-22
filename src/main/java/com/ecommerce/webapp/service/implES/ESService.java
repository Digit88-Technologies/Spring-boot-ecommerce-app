package com.ecommerce.webapp.service.implES;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.ecommerce.webapp.model.Es_products;
import com.ecommerce.webapp.util.ESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.function.Supplier;

@Service
public class ESService {

   @Autowired
   private ElasticsearchClient  elasticsearchClient;


    public SearchResponse<Es_products> autoSuggestProduct(String partialProductName) throws IOException {

        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(partialProductName);
       SearchResponse<Es_products> searchResponse  = elasticsearchClient
                .search(s->s.index("products").query(supplier.get()), Es_products.class);
        System.out.println(" elasticsearch auto suggestion query"+supplier.get().toString());
        return searchResponse;
    }

}