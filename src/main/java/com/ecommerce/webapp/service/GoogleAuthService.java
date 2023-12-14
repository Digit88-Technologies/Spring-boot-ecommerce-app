package com.ecommerce.webapp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


/**
 * Service interface for handling Google authentication.
 */
public interface GoogleAuthService {

    /**
     * Handles Google callback to extract token using authorization code.
     *
     * @param authorizationCode The authorization code received from Google.
     * @param request           The HTTP servlet request.
     * @return ModelAndView containing the result of the Google authentication.
     */
    ModelAndView handleGoogleCallback(String authorizationCode, HttpServletRequest request);
}
