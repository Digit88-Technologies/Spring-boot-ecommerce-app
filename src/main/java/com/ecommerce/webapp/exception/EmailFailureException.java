package com.ecommerce.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception to highlight that we were unable to send an email.
 */
public class EmailFailureException extends Exception {

    public EmailFailureException(String errorSendingRegistrationEmail, String username, EmailFailureException e) {
    }

    public static void handleException(String message, String username, Exception ex) {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, message + " for user: " + username, ex);
    }
}
