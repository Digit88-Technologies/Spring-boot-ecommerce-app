package com.ecommerce.webapp.controller.socialLogin;

import com.ecommerce.webapp.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class GoogleLoginController {

    public static final String REDIRECT_URI = "http://localhost:8082/login/oauth2/code/google";
    public static final String SCOPE = "openid profile email";
    public static final String AUTH_STATE_CODE = "ndbjuww88w8wheheb72828whehd8w8";
    public static final String TOKEN_ENDPOINT = "/login/oauth2/code/google";
    public static final String GOOGLE_LOGIN = "/google/login";
    private final GoogleAuthService googleAuthService;

    private final ClientRegistrationRepository clientRegistrationRepository;

    public GoogleLoginController(GoogleAuthService googleAuthService, ClientRegistrationRepository clientRegistrationRepository) {
        this.googleAuthService = googleAuthService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }


    @GetMapping(GOOGLE_LOGIN)
    public String googleLogin() {

        log.info("Google login Request");
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(REDIRECT_URI)
                .scope(SCOPE)
                .state(AUTH_STATE_CODE)
                .build();

        log.info("Redirecting to Google auth endpoint to select preferred social account further");
        return "redirect:" + authorizationRequest.getAuthorizationRequestUri();
    }

    @GetMapping(TOKEN_ENDPOINT)
    public ModelAndView handleGoogleCallback(@RequestParam(name = "code") String authorizationCode, HttpServletRequest request) {

        log.info("Handling auth response from Google for user registration");
        return googleAuthService.handleGoogleCallback(authorizationCode, request);

    }

}
