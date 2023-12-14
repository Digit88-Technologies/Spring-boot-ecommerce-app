package com.ecommerce.webapp.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.servlet.ModelAndView;


/**
 * Service interface for handling Facebook authentication.
 */
public interface FaceBookAuthService {

    /**
     * Handles Facebook callback to extract token using authorization code.
     *
     * @param authorizationCode The authorization code received from Facebook.
     * @param request           The HTTP servlet request.
     * @return ModelAndView containing the result of the Facebook authentication.
     */
    ModelAndView handleFacebookCallback(String authorizationCode, HttpServletRequest request);
}
