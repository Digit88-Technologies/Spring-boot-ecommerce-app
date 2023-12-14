package com.ecommerce.webapp.service;

/**
 * Service interface for handling encryption of passwords.
 */
public interface EncryptionService {

    /**
     * Encrypts the given password.
     *
     * @param password The plain text password.
     * @return The encrypted password.
     */
    String encryptPassword(String password);

    /**
     * Verifies that a password is correct.
     *
     * @param password The plain text password.
     * @param hash     The encrypted password.
     * @return True if the password is correct, false otherwise.
     */
    boolean verifyPassword(String password, String hash);
}
