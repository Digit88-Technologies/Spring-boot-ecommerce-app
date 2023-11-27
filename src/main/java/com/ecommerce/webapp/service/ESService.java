package com.ecommerce.webapp.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecommerce.webapp.model.ProductsESIndex;
import com.ecommerce.webapp.util.ESUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Service
public class ESService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;


    public List<String> autoSuggestProduct(String partialProductName) throws IOException {

        Supplier<Query> supplier = ESUtil.createSupplierAutoSuggest(partialProductName);
        SearchResponse<ProductsESIndex> searchResponse = elasticsearchClient
                .search(s -> s.index("products").query(supplier.get()), ProductsESIndex.class);
        List<Hit<ProductsESIndex>> hitList = searchResponse.hits().hits();
        List<ProductsESIndex> productList = new ArrayList<>();
        for (Hit<ProductsESIndex> hit : hitList) {
            productList.add(hit.source());
        }
        List<String> listOfProductNames = new ArrayList<>();
        for (ProductsESIndex product : productList) {
            listOfProductNames.add(product.getName());
        }
        return listOfProductNames;
    }

}