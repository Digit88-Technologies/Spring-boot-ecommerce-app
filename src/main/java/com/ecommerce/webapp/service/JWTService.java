package com.ecommerce.webapp.service;

import com.ecommerce.webapp.exception.IllegalProviderArgumentException;
import com.ecommerce.webapp.model.LocalUser;
import org.springframework.web.servlet.ModelAndView;

/**
 * Service interface for handling JWTs for user authentication.
 */
public interface JWTService {

    /**
     * Generates a JWT based on the given user.
     *
     * @param user The user to generate for.
     * @return The JWT.
     */
    String generateJWT(LocalUser user);

    /**
     * Generates a special token for verification of an email.
     *
     * @param user The user to create the token for.
     * @return The token generated.
     */
    String generateVerificationJWT(LocalUser user);

    /**
     * Generates a JWT for use when resetting a password.
     *
     * @param user The user to generate for.
     * @return The generated JWT token.
     */
    String generatePasswordResetJWT(LocalUser user);

    /**
     * Gets the email from a password reset token.
     *
     * @param token The token to use.
     * @return The email in the token if valid.
     */
    String getResetPasswordEmail(String token);

    /**
     * Gets the username out of a given JWT.
     *
     * @param token        The JWT to decode.
     * @param providerName The name of the provider (e.g., "google" or "facebook").
     * @return The username stored inside.
     */
    String getUsername(String token, String providerName) throws IllegalProviderArgumentException;


}
