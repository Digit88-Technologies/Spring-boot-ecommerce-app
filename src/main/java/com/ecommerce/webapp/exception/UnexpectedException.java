package com.ecommerce.webapp.exception;

import com.ecommerce.webapp.api.controller.auth.AuthenticationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnexpectedException extends Exception {

    private static final Logger logger = LoggerFactory.getLogger(UnexpectedException.class);

    public UnexpectedException(String unexpectedErrorDuringUserRegistration, String username, Exception e) {
    }
    public static void handleUnexpectedException(String message, String username, Exception e) {
        logger.error("{} for user: {}", message, username, e);
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
    }


}
