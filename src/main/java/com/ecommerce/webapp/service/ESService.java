package com.ecommerce.webapp.service;

import java.io.IOException;
import java.util.List;

/**
 * Service interface for handling Elasticsearch operations.
 */
public interface ESService {

    /**
     * Retrieves a list of product names based on partial product name for auto-suggest.
     *
     * @param partialProductName The partial product name for auto-suggest.
     * @return List of product names.
     * @throws IOException If an error occurs during Elasticsearch operation.
     */
    List<String> autoSuggestProduct(String partialProductName) throws IOException;
}
