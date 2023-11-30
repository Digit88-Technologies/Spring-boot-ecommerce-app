package com.ecommerce.webapp.service;

import com.ecommerce.webapp.config.TwilioConfig;
import com.ecommerce.webapp.exception.UserAlreadyExistsException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.dao.LocalUserDAO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class FaceBookAuthService {

    public static final String CODE = "code";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String REDIRECT_URL = "http://localhost:8082/login/oauth2/code/facebook";
    public static final String TOKEN_URL = "https://graph.facebook.com/oauth/access_token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String USER_INFO_URL = "https://graph.facebook.com/me";
    public static final String USER_EXISTS_IN_THE_DATABASE = "User Exists in the database";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String NAME1 = "first_name";
    public static final String NAME2 = "last_name";
    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.facebook.redirect-uri}")
    private String redirectUri;

    @Autowired
    private RestTemplate restTemplate;
    private final LocalUserDAO localUserDAO;
    @Autowired
    private TwilioConfig twilioConfig;

    Map<String, String> otpMap = new HashMap<>();


    private final ClientRegistrationRepository clientRegistrationRepository;

    public FaceBookAuthService(LocalUserDAO localUserDAO, ClientRegistrationRepository clientRegistrationRepository) {
        this.localUserDAO = localUserDAO;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public ModelAndView handleFacebookCallback(String authorizationCode, HttpServletRequest request) {

        log.info("Handling Facebook auth request and extracting token using authorization code");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("facebook");

        Map<String, String> params = new HashMap<>();
        params.put(CODE, authorizationCode);
        params.put(CLIENT_ID, clientRegistration.getClientId());
        params.put(CLIENT_SECRET, clientRegistration.getClientSecret());
        params.put(REDIRECT_URI, REDIRECT_URL);

        HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, httpRequest, Map.class);
        Map<String, Object> responseBody = response.getBody();

        String accessToken = (String) responseBody.get(ACCESS_TOKEN);

        log.info("Fetching user information against token received");

        RestTemplate userInfoRestTemplate = new RestTemplate();
        HttpHeaders userInfoHeaders = new HttpHeaders();

        userInfoHeaders.setBearerAuth(accessToken);
        userInfoHeaders.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(USER_INFO_URL)
                .queryParam("fields", "id,email,name,first_name,last_name");

        HttpEntity<Void> httpUserInfoRequest = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<Map> httpUserInfoResponse = userInfoRestTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                httpUserInfoRequest,
                Map.class
        );

        Map<String, Object> userDetails = httpUserInfoResponse.getBody();

        log.info("Verifying auth user against database...");
        if (localUserDAO.findByEmailIgnoreCase((String) userDetails.get("email")).isPresent()
                || localUserDAO.findByUsernameIgnoreCase((String) userDetails.get("name")).isPresent()) {
            throw new UserAlreadyExistsException(USER_EXISTS_IN_THE_DATABASE);
        } else {

            log.info("Creating a new user into the system");

            LocalUser googleUserAsLocalUser = new LocalUser();
            googleUserAsLocalUser.setUsername((String) userDetails.get(NAME));
            googleUserAsLocalUser.setEmail((String) userDetails.get(EMAIL));
            googleUserAsLocalUser.setFirstName((String) userDetails.get(NAME1));
            googleUserAsLocalUser.setLastName((String) userDetails.get(NAME2));
            googleUserAsLocalUser.setEmailVerified(true);

            localUserDAO.save(googleUserAsLocalUser);
        }

        ModelAndView modelAndView = new ModelAndView("Welcome");
        modelAndView.addObject("accessToken", accessToken);
        modelAndView.addObject("userName", (String) userDetails.get("name"));

        return modelAndView;
    }
}
