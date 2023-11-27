package com.ecommerce.webapp.api.controller.socialLogin;

import com.ecommerce.webapp.service.GoogleAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
public class GoogleLoginController {

    private final GoogleAuthService googleAuthService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public GoogleLoginController(GoogleAuthService googleAuthService, ClientRegistrationRepository clientRegistrationRepository) {
        this.googleAuthService = googleAuthService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }


    @GetMapping("/google/login")
    public String googleLogin() {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri("http://localhost:8082/login/oauth2/code/google")
                .scope("openid profile email")
                .state("ndbjuww88w8wheheb72828whehd8w8")
                .build();

        return "redirect:" + authorizationRequest.getAuthorizationRequestUri();
    }

    @GetMapping("/login/oauth2/code/google")
    public ModelAndView handleGoogleCallback(@RequestParam(name = "code") String authorizationCode, HttpServletRequest request) {

        return googleAuthService.handleGoogleCallback(authorizationCode, request);

    }

}
