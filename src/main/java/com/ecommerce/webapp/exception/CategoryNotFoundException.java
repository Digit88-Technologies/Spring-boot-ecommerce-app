package com.ecommerce.webapp.exception;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(String category) {
        super("Category not found: " + category);
    }
}
