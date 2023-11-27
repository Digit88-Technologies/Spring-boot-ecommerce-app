package com.ecommerce.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

public class IOExceptionHandler extends RuntimeException {

    public static void handleIOException(String message, IOException e) {
        // You can log the exception or perform any specific actions here.
        // For now, we are throwing a ResponseStatusException with an internal server error status.
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message, e);
    }
}
