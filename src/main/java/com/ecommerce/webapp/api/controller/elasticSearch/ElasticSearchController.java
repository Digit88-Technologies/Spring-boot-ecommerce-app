package com.ecommerce.webapp.api.controller.elasticSearch;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.ecommerce.webapp.exception.IOExceptionHandler;
import com.ecommerce.webapp.model.ProductsESIndex;
import com.ecommerce.webapp.model.dao.ElasticSearchRepository;
import com.ecommerce.webapp.service.ESService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for ElasticSearch operations.
 */
@RestController
@RequestMapping("/elastic")
public class ElasticSearchController {

    @Autowired
    private ElasticSearchRepository elasticSearchQuery;

    @Autowired
    private ESService esService;

    /**
     * Create or update a document in ElasticSearch.
     *
     * @param product The product to be created or updated.
     * @return ResponseEntity with the result of the operation.
     */
    @PostMapping("/createOrUpdateDocument")
    public ResponseEntity<Object> createOrUpdateDocument(@RequestBody ProductsESIndex product) {
        try {
            String response = elasticSearchQuery.createOrUpdateDocument(product);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            IOExceptionHandler.handleIOException("Error creating or updating document", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Get a document from ElasticSearch by ID.
     *
     * @param productId The ID of the product.
     * @return ResponseEntity with the retrieved product.
     */
    @GetMapping("/getDocument")
    public ResponseEntity<Object> getDocumentById(@RequestParam String productId) {
        try {
            ProductsESIndex product = elasticSearchQuery.getDocumentById(productId);
            return new ResponseEntity<>(product, HttpStatus.OK);
        } catch (IOException e) {
            IOExceptionHandler.handleIOException("Error retrieving document by ID", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Delete a document from ElasticSearch by ID.
     *
     * @param productId The ID of the product.
     * @return ResponseEntity with the result of the operation.
     */
    @DeleteMapping("/deleteDocument")
    public ResponseEntity<Object> deleteDocumentById(@RequestParam String productId) {
        try {
            String response = elasticSearchQuery.deleteDocumentById(productId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            IOExceptionHandler.handleIOException("Error deleting document by ID", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Search for all documents in ElasticSearch.
     *
     * @return ResponseEntity with the list of products.
     */
    @GetMapping("/searchDocument")
    public ResponseEntity<Object> searchAllDocument() {
        try {
            List<ProductsESIndex> products = elasticSearchQuery.searchAllDocuments();
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (IOException e) {
            IOExceptionHandler.handleIOException("Error searching for all documents", e);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * Auto-suggest product names based on a partial product name.
     *
     * @param partialProductName The partial product name for auto-suggestion.
     * @return List of suggested product names.
     */
    @GetMapping("/autoSuggest/{partialProductName}")
    List<String> autoSuggestProductSearch(@PathVariable String partialProductName) {
        try {
            SearchResponse<ProductsESIndex> searchResponse = esService.autoSuggestProduct(partialProductName);
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
        } catch (IOException e) {
            IOExceptionHandler.handleIOException("Error performing auto-suggest product search", e);
        }
        return new ArrayList<>();
    }

}