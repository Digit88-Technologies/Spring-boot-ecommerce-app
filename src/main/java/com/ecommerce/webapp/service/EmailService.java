package com.ecommerce.webapp.service;

import com.ecommerce.webapp.exception.EmailFailureException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.VerificationToken;

import jakarta.mail.MessagingException;
import java.io.UnsupportedEncodingException;

/**
 * Service interface for handling emails being sent.
 */
public interface EmailService {

    void sendVerificationEmail(VerificationToken verificationToken) throws EmailFailureException, MessagingException, UnsupportedEncodingException;

    void sendWelcomeEmail(LocalUser user) throws EmailFailureException, MessagingException, UnsupportedEncodingException;

    void sendPasswordResetEmail(LocalUser user, String token) throws EmailFailureException, MessagingException, UnsupportedEncodingException;
}
