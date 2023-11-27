package com.ecommerce.webapp.service;

import com.ecommerce.webapp.api.security.TwilioConfig;
import com.ecommerce.webapp.exception.UserAlreadyExistsException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.dao.LocalUserDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
        params.put("code", authorizationCode);
        params.put("client_id", clientRegistration.getClientId());
        params.put("client_secret", clientRegistration.getClientSecret());
        params.put("redirect_uri", "http://localhost:8082/login/oauth2/code/google");
        params.put("grant_type", "authorization_code");
        params.put("access_type", "offline");  // To fetch refresh token

        HttpEntity<Map<String, String>> httpRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity("https://oauth2.googleapis.com/token", httpRequest, Map.class);
        Map<String, Object> responseBody = response.getBody();

        String accessToken = (String) responseBody.get("access_token");
        String refreshToken = (String) responseBody.get("refresh_token");

        System.out.println("Access Token : " + accessToken);
        System.out.println("Refresh Token : " + refreshToken);

//        HttpSession session = request.getSession();
//        session.setAttribute("refresh_token", refreshToken);


        //Fetching User details from google and storing the user information in the database
        RestTemplate userInfoRestTemplate = new RestTemplate();
        HttpHeaders userInfoHeaders = new HttpHeaders();

// Set the Authorization header with the access token
        userInfoHeaders.setBearerAuth(accessToken);
        userInfoHeaders.setContentType(MediaType.APPLICATION_JSON);

// Make a GET request to the userinfo endpoint
        HttpEntity<Void> httpUserInfoRequest = new HttpEntity<>(userInfoHeaders);
        ResponseEntity<Map> httpUserInfoResponse = userInfoRestTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.GET,
                httpUserInfoRequest,
                Map.class
        );

// Extract user details from the response
        Map<String, Object> userDetails = httpUserInfoResponse.getBody();

// Now you can use 'userDetails' as needed
        System.out.println("User Details: " + userDetails);

        if (localUserDAO.findByEmailIgnoreCase((String) userDetails.get("email")).isPresent()
                || localUserDAO.findByUsernameIgnoreCase((String) userDetails.get("name")).isPresent()) {
            System.out.println("User Exists in the database");
            throw new UserAlreadyExistsException("User already exists");
        } else {

            LocalUser googleUserAsLocalUser = new LocalUser();
            googleUserAsLocalUser.setUsername((String) userDetails.get("name"));
            googleUserAsLocalUser.setEmail((String) userDetails.get("email"));
            googleUserAsLocalUser.setFirstName((String) userDetails.get("given_name"));
            googleUserAsLocalUser.setLastName((String) userDetails.get("family_name"));
            googleUserAsLocalUser.setEmailVerified(true);

            localUserDAO.save(googleUserAsLocalUser);
        }
        // Redirect to the home page or wherever you need
        ModelAndView modelAndView = new ModelAndView("Welcome");
        modelAndView.addObject("accessToken", accessToken);
        modelAndView.addObject("userName", (String) userDetails.get("name"));

        return modelAndView;
    }
}
