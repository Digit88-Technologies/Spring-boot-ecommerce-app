package com.ecommerce.webapp.api.controller.socialLogin;

import com.ecommerce.webapp.service.FaceBookAuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class FacebookLoginController {

    public static final String REDIRECT_URI = "http://localhost:8082/login/oauth2/code/facebook";
    public static final String SCOPE = "public_profile email";
    public static final String AUTH_STATE_CODE = "ndbjuww88w8wheheb72828whehd8w8";
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
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("facebook");
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .clientId(clientRegistration.getClientId())
                .authorizationUri(clientRegistration.getProviderDetails().getAuthorizationUri())
                .redirectUri(REDIRECT_URI)
                .scope(SCOPE)
                .build();

        return "redirect:" + authorizationRequest.getAuthorizationRequestUri();
    }

    @GetMapping(TOKEN_ENDPOINT)
    public ModelAndView handleFacebookCallback(@RequestParam(name = "code") String authorizationCode, HttpServletRequest request) {
        return facebookAuthService.handleFacebookCallback(authorizationCode, request);

    }
}

