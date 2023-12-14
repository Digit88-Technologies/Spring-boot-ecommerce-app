package com.ecommerce.webapp.controller.socialLogin;

import com.ecommerce.webapp.service.FaceBookAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@Controller
public class FacebookLoginController {

    public static final String REDIRECT_URI = "http://localhost:8082/login/oauth2/code/facebook";
    public static final String SCOPE = "public_profile email";
    public static final String TOKEN_ENDPOINT = "/login/oauth2/code/facebook";
    public static final String FACEBOOK_LOGIN = "/facebook/login";
    private final FaceBookAuthService facebookAuthService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public FacebookLoginController(FaceBookAuthService facebookAuthService, ClientRegistrationRepository clientRegistrationRepository) {
        this.facebookAuthService = facebookAuthService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @GetMapping(FACEBOOK_LOGIN)
    public String facebookLogin() {

        log.info("Facebook Login Request");
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("facebook");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(REDIRECT_URI)
                .scope(SCOPE)
                .build();

        log.info("Redirecting user to auth endpoint to select preferred social account further");
        return "redirect:" + authorizationRequest.getAuthorizationRequestUri();
    }

    @GetMapping(TOKEN_ENDPOINT)
    public ModelAndView handleFacebookCallback(@RequestParam(name = "code") String authorizationCode, HttpServletRequest request) {

        log.info("Handling auth response for user registration");
        return facebookAuthService.handleFacebookCallback(authorizationCode, request);

    }
}

