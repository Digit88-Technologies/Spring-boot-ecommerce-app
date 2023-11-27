package com.ecommerce.webapp.exception;

public class CategoryNotFoundException extends RuntimeException {

    public static final String CATEGORY_NOT_FOUND = "Category not found: ";

    public CategoryNotFoundException(String category) {
        super(CATEGORY_NOT_FOUND + category);
    }
}
