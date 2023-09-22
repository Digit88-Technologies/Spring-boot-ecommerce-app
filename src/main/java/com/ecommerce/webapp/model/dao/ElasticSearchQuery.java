package com.ecommerce.webapp.model.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecommerce.webapp.model.Es_products;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class ElasticSearchQuery {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final String indexName = "products";


    public String createOrUpdateDocument(Es_products product) throws IOException {

        IndexResponse response = elasticsearchClient.index(i -> i
                .index(indexName)
                .id(product.getId())
                .document(product)
        );
        if(response.result().name().equals("Created")){
            return new StringBuilder("Document has been successfully created.").toString();
        }else if(response.result().name().equals("Updated")){
            return new StringBuilder("Document has been successfully updated.").toString();
        }
        return new StringBuilder("Error while performing the operation.").toString();
    }

    public Es_products getDocumentById(String productId) throws IOException{
        Es_products product = null;
        GetResponse<Es_products> response = elasticsearchClient.get(g -> g
                        .index(indexName)
                        .id(productId),
                Es_products.class
        );

        if (response.found()) {
             product = response.source();
            System.out.println("Es_products name " + product.getName());
        } else {
            System.out.println ("Es_products not found");
        }

       return product;
    }

    public String deleteDocumentById(String productId) throws IOException {

        DeleteRequest request = DeleteRequest.of(d -> d.index(indexName).id(productId));

        DeleteResponse deleteResponse = elasticsearchClient.delete(request);
        if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
            return new StringBuilder("Es_products with id " + deleteResponse.id() + " has been deleted.").toString();
        }
        System.out.println("Es_products not found");
        return new StringBuilder("Es_products with id " + deleteResponse.id()+" does not exist.").toString();

    }

    public  List<Es_products> searchAllDocuments() throws IOException {

        SearchRequest searchRequest =  SearchRequest.of(s -> s.index(indexName));
        SearchResponse searchResponse =  elasticsearchClient.search(searchRequest, Es_products.class);
        List<Hit> hits = searchResponse.hits().hits();
        List<Es_products> products = new ArrayList<>();
        for(Hit object : hits){

            System.out.print(((Es_products) object.source()));
            products.add((Es_products) object.source());

        }
        return products;
    }
}