package com.ecommerce.webapp.controller.elasticSearch;

import com.ecommerce.webapp.model.ProductsESIndex;
import com.ecommerce.webapp.repository.ElasticSearchRepository;
import com.ecommerce.webapp.service.ESService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for ElasticSearch operations.
 */
@Slf4j
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
    public ResponseEntity<Object> createOrUpdateDocument(@RequestBody ProductsESIndex product) throws IOException {
        log.info("Creating / Updating Index Documents In ElasticSearch");
        String response = elasticSearchQuery.createOrUpdateDocument(product);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get a document from ElasticSearch by ID.
     *
     * @param productId The ID of the product.
     * @return ResponseEntity with the retrieved product.
     */
    @GetMapping("/getDocument")
    public ResponseEntity<Object> getDocumentById(@RequestParam String productId) throws IOException {
        log.info("Getting an Index document from ElasticSearch");
        ProductsESIndex product = elasticSearchQuery.getDocumentById(productId);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    /**
     * Delete a document from ElasticSearch by ID.
     *
     * @param productId The ID of the product.
     * @return ResponseEntity with the result of the operation.
     */
    @DeleteMapping("/deleteDocument")
    public ResponseEntity<Object> deleteDocumentById(@RequestParam String productId) throws IOException {
        log.info("Deleting Suggested Index document");
        String response = elasticSearchQuery.deleteDocumentById(productId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Search for all documents in ElasticSearch.
     *
     * @return ResponseEntity with the list of products.
     */
    @GetMapping("/searchDocument")
    public ResponseEntity<Object> searchAllDocument() throws IOException {
        log.info("Fetching All Index Documents");
        List<ProductsESIndex> products = elasticSearchQuery.searchAllDocuments();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    /**
     * Auto-suggest product names based on a partial product name.
     *
     * @param partialProductName The partial product name for auto-suggestion.
     * @return List of suggested product names.
     */
    @GetMapping("/autoSuggest/{partialProductName}")
    List<String> autoSuggestProductSearch(@PathVariable String partialProductName) throws IOException {
        log.info("Fetching Products Based on Partial Product Name using text auto-suggest on ElasticSearch");
        return esService.autoSuggestProduct(partialProductName);

    }

}