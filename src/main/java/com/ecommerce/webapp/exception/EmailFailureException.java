package com.ecommerce.webapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception to highlight that we were unable to send an email.
 */
public class EmailFailureException extends RuntimeException {

    public EmailFailureException(String errorSendingRegistrationEmail, String username, MailException e) {
    }

}
