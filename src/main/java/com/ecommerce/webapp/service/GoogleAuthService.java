package com.ecommerce.webapp.service;

import com.ecommerce.webapp.config.TwilioConfig;
import com.ecommerce.webapp.exception.UserAlreadyExistsException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.dao.LocalUserDAO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleAuthService {

    public static final String CODE = "code";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String GRANT_TYPE = "grant_type";
    public static final String ACCESS_TYPE = "access_type";
    public static final String OFFLINE = "offline";
    public static final String AUTHORIZATION_CODE = "authorization_code";
    public static final String REDIRECT_URL = "http://localhost:8082/login/oauth2/code/google";
    public static final String TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String USER_INFO_URL = "https://openidconnect.googleapis.com/v1/userinfo";
    public static final String USER_EXISTS_IN_THE_DATABASE = "User Exists in the database";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String NAME1 = "given_name";
    public static final String NAME2 = "family_name";
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    @Autowired
    private RestTemplate restTemplate;
    private final LocalUserDAO localUserDAO;
    @Autowired
    private TwilioConfig twilioConfig;

    Map<String, String> otpMap = new HashMap<>();


    private final ClientRegistrationRepository clientRegistrationRepository;

    public GoogleAuthService(LocalUserDAO localUserDAO, ClientRegistrationRepository clientRegistrationRepository) {
        this.localUserDAO = localUserDAO;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public ModelAndView handleGoogleCallback(String authorizationCode, HttpServletRequest request) {

        System.out.println("Authorization Code: " + authorizationCode);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");

        Map<String, String> params = new HashMap<>();
        params.put(CODE, authorizationCode);
        params.put(CLIENT_ID, clientRegistration.getClientId());
        params.put(CLIENT_SECRET, clientRegistration.getClientSecret());
        params.put(REDIRECT_URI, REDIRECT_URL);
        params.put(GRANT_TYPE, AUTHORIZATION_CODE);
        params.put(ACCESS_TYPE, OFFLINE);  // To fetch refresh token

        HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(TOKEN_URL, httpRequest, Map.class);
        Map<String, Object> responseBody = response.getBody();

        String accessToken = (String) responseBody.get(ACCESS_TOKEN);

        //Fetching User details from google and storing the user information in the database
        RestTemplate userInfoRestTemplate = new RestTemplate();
        HttpHeaders userInfoHeaders = new HttpHeaders();

            userInfoHeaders.setBearerAuth(accessToken);
        userInfoHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Making a GET request to the userinfo endpoint
        HttpEntity<Void> httpUserInfoRequest = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<Map> httpUserInfoResponse = userInfoRestTemplate.exchange(
                USER_INFO_URL,
                HttpMethod.GET,
                httpUserInfoRequest,
                Map.class
        );

        // Extract user details from the response
        Map<String, Object> userDetails = httpUserInfoResponse.getBody();

        System.out.println("User Details: " + userDetails);

        if (localUserDAO.findByEmailIgnoreCase((String) userDetails.get("email")).isPresent()
                || localUserDAO.findByUsernameIgnoreCase((String) userDetails.get("name")).isPresent()) {
            throw new UserAlreadyExistsException(USER_EXISTS_IN_THE_DATABASE);
        } else {

            LocalUser googleUserAsLocalUser = new LocalUser();
            googleUserAsLocalUser.setUsername((String) userDetails.get(NAME));
            googleUserAsLocalUser.setEmail((String) userDetails.get(EMAIL));
            googleUserAsLocalUser.setFirstName((String) userDetails.get(NAME1));
            googleUserAsLocalUser.setLastName((String) userDetails.get(NAME2));
            googleUserAsLocalUser.setEmailVerified(true);

            localUserDAO.save(googleUserAsLocalUser);
        }
        // Redirecting to the home page
        ModelAndView modelAndView = new ModelAndView("Welcome");
        modelAndView.addObject("accessToken", accessToken);
        modelAndView.addObject("userName", (String) userDetails.get("name"));

        return modelAndView;
    }
}
