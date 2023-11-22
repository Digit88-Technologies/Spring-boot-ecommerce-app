package com.ecommerce.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Exception thrown at user registration if an existing user already exists
 * with the given information.
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String userAlreadyExists) {
    }
}
