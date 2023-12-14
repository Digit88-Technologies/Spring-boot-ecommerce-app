package com.ecommerce.webapp.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecommerce.webapp.model.ProductsESIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Repository
public class ElasticSearchRepository {

    public static final String SUCCESSFULLY_CREATED = "Document has been successfully created.";
    public static final String SUCCESSFULLY_UPDATED = "Document has been successfully updated.";
    public static final String ERROR_PERFORMING_OPERATION = "Error while performing the operation.";
    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final String indexName = "products";


    public String createOrUpdateDocument(ProductsESIndex product) throws IOException {

        IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(product.getId())
                .document(product)
        );
        if (response.result().name().equals("Created")) {
            return SUCCESSFULLY_CREATED;
        } else if (response.result().name().equals("Updated")) {
            return SUCCESSFULLY_UPDATED;
        }
        return ERROR_PERFORMING_OPERATION;
    }

    public ProductsESIndex getDocumentById(String productId) throws IOException {
        ProductsESIndex product = null;
        GetResponse<ProductsESIndex> response = elasticsearchClient.get(g -> g
                        .index(indexName)
                        .id(productId),
                ProductsESIndex.class
        );

        if (response.found()) {
            product = response.source();
        } else {
            throw new NoSuchElementException();
        }

        return product;
    }

    public String deleteDocumentById(String productId) throws IOException {

        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(productId));

        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            return "Index document with id " + deleteResponse.id() + " has been deleted.";
        }
        System.out.println("Index not found");
        return "ProductsESIndex with id " + deleteResponse.id() + " does not exist.";

    }

    public List<ProductsESIndex> searchAllDocuments() throws IOException {

        SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName));
        SearchResponse searchResponse = elasticsearchClient.search(searchRequest, ProductsESIndex.class);
        List<Hit> hits = searchResponse.hits().hits();
        List<ProductsESIndex> products = new ArrayList<>();
        for (Hit object : hits) {

            products.add((ProductsESIndex) object.source());

        }
        return products;
    }
}


















