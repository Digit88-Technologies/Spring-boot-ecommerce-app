package com.ecommerce.webapp.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ecommerce.webapp.exception.IllegalProviderArgumentException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.service.JWTService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.Map;

/**
 * Service for handling JWTs for user authentication.
 */
@Service
public class JWTServiceImpl implements JWTService {

    public static final String GOOGLE_USER_INFO_URL = "https://openidconnect.googleapis.com/v1/userinfo";

    public static final String GOOGLE_TOKEN_INFO_URL = "https://www.googleapis.com/oauth2/v1/tokeninfo";
    public static final String FACEBOOK_USER_INFO_URL = "https://graph.facebook.com/me";
    public static final String FIELDS_VALUE = "id,email,name,first_name,last_name";
    public static final String FIELDS = "fields";
    public static final String FACEBOOK_TOKEN_INFO_URL = "https://graph.facebook.com/v13.0/debug_token";
    public static final String FACEBOOK_TOKEN_INFO_QUERY = "366101749211173|8885834671f187560b82e2c0f5244b76";
    /**
     * The secret key to encrypt the JWTs with.
     */
    @Value("${jwt.algorithm.key}")
    private String algorithmKey;
    /**
     * The issuer the JWT is signed with.
     */
    @Value("${jwt.issuer}")
    private String issuer;
    /**
     * How many seconds from generation should the JWT expire?
     */
    @Value("${jwt.expiryInSeconds}")
    private int expiryInSeconds;
    /**
     * The algorithm generated post construction.
     */
    private Algorithm algorithm;
    /**
     * The JWT claim key for the username.
     */
    private static final String USERNAME_KEY = "USERNAME";
    private static final String VERIFICATION_EMAIL_KEY = "VERIFICATION_EMAIL";
    private static final String RESET_PASSWORD_EMAIL_KEY = "RESET_PASSWORD_EMAIL";

    /**
     * Post construction method.
     */
    @PostConstruct
    public void postConstruct() {
        algorithm = Algorithm.HMAC256(algorithmKey);
    }

    /**
     * Generates a JWT based on the given user.
     *
     * @param user The user to generate for.
     * @return The JWT.
     */
    public String generateJWT(LocalUser user) {
        return JWT.create()
                .withClaim(USERNAME_KEY, user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Generates a special token for verification of an email.
     *
     * @param user The user to create the token for.
     * @return The token generated.
     */
    public String generateVerificationJWT(LocalUser user) {
        return JWT.create()
                .withClaim(VERIFICATION_EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * expiryInSeconds)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Generates a JWT for use when resetting a password.
     *
     * @param user The user to generate for.
     * @return The generated JWT token.
     */
    public String generatePasswordResetJWT(LocalUser user) {
        return JWT.create()
                .withClaim(RESET_PASSWORD_EMAIL_KEY, user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 30)))
                .withIssuer(issuer)
                .sign(algorithm);
    }

    /**
     * Gets the email from a password reset token.
     *
     * @param token The token to use.
     * @return The email in the token if valid.
     */
    public String getResetPasswordEmail(String token) {
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(RESET_PASSWORD_EMAIL_KEY).asString();
    }

    /**
     * Gets the username out of a given JWT.
     *
     * @param token The JWT to decode.
     * @return The username stored inside.
     */
    public String getUsername(String token, String providername) {

        try {
            return getUsernameFromToken(token);
        } catch (Exception e) {

            return getAlternativeUsername(token, providername);
        }
    }

    /**
     * Retrieves the username from a JWT (JSON Web Token).
     *
     * @param token The JWT from which the username will be extracted.
     * @return The username extracted from the JWT.
     */
    private String getUsernameFromToken(String token) {
        DecodedJWT jwt = JWT.require(algorithm).withIssuer(issuer).build().verify(token);
        return jwt.getClaim(USERNAME_KEY).asString();
    }

    /**
     * Retrieves an alternative username or user information from a specified identity provider (Google or Facebook).
     *
     * @param token    The access token obtained from the identity provider.
     * @param provider The identity provider (either "google" or "facebook").
     * @return The username or user information obtained from the identity provider.
     * @throws IllegalArgumentException If an unsupported identity provider is provided.
     */
    private String getAlternativeUsername(String token, String provider) throws IllegalProviderArgumentException {

        if (!isTokenValid(token, provider)) {
            throw new IllegalProviderArgumentException("Invalid or expired token", provider);
        }

        RestTemplate userInfoRestTemplate = new RestTemplate();
        HttpHeaders userInfoHeaders = new HttpHeaders();

        userInfoHeaders.setBearerAuth(token);
        userInfoHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map> httpUserInfoResponse;

        if ("google".equalsIgnoreCase(provider)) {
            httpUserInfoResponse = userInfoRestTemplate.exchange(
                    GOOGLE_USER_INFO_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(userInfoHeaders),
                    Map.class
            );
        } else if ("facebook".equalsIgnoreCase(provider)) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(FACEBOOK_USER_INFO_URL)
                    .queryParam(FIELDS, FIELDS_VALUE);

            httpUserInfoResponse = userInfoRestTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    new HttpEntity<>(userInfoHeaders),
                    Map.class
            );
        } else {
            throw new IllegalProviderArgumentException("Unsupported identity provider", provider);
        }

        Map<String, Object> userDetails = httpUserInfoResponse.getBody();

        return (String) userDetails.get("name");
    }

    private boolean isTokenValid(String token, String provider) {
        try {

            RestTemplate tokenInfoRestTemplate = new RestTemplate();
            HttpHeaders tokenInfoHeaders = new HttpHeaders();

            tokenInfoHeaders.setBearerAuth(token);
            tokenInfoHeaders.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> httpTokenInfoResponse = null;

            if ("google".equalsIgnoreCase(provider)) {
                httpTokenInfoResponse = tokenInfoRestTemplate.exchange(
                        GOOGLE_TOKEN_INFO_URL,
                        HttpMethod.POST,
                        new HttpEntity<>(tokenInfoHeaders),
                        Map.class
                );
                Map<String, Object> tokenInfo = httpTokenInfoResponse.getBody();

                return tokenInfo != null && !tokenInfo.containsKey("error");
            } else if ("facebook".equalsIgnoreCase(provider)) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(FACEBOOK_TOKEN_INFO_URL)
                        .queryParam("input_token", token)
                        .queryParam("access_token", FACEBOOK_TOKEN_INFO_QUERY);

                httpTokenInfoResponse = new RestTemplate().exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        null,
                        Map.class
                );

                Map<String, Object> tokenInfo = httpTokenInfoResponse.getBody();
                if (tokenInfo != null && tokenInfo.containsKey("data")) {
                    Map<String, Object> data = (Map<String, Object>) tokenInfo.get("data");
                    return data.containsKey("is_valid") && (boolean) data.get("is_valid");
                }

            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Something is Wrong With Token!");
        }
        return false;
    }


}
