package com.ecommerce.webapp.api.security;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ecommerce.webapp.model.LocalUser;
import com.ecommerce.webapp.model.dao.LocalUserDAO;
import com.ecommerce.webapp.service.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Filter for decoding a JWT in the Authorization header and loading the user
 * object into the authentication context.
 */
@Component
public class JWTRequestFilter extends OncePerRequestFilter implements ChannelInterceptor {

    private JWTService jwtService;
    private LocalUserDAO localUserDAO;

    public JWTRequestFilter(JWTService jwtService, LocalUserDAO localUserDAO) {
        this.jwtService = jwtService;
        this.localUserDAO = localUserDAO;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenHeader = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken token = checkToken(tokenHeader);
        if (token != null) {
            token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Method to authenticate a token and return the Authentication object
     * written to the spring security context.
     *
     * @param token The token to test.
     * @return The Authentication object if set.
     */
    private UsernamePasswordAuthenticationToken checkToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String username = jwtService.getUsername(token);
                Optional<LocalUser> opUser = localUserDAO.findByUsernameIgnoreCase(username);
                if (opUser.isPresent()) {
                    LocalUser user = opUser.get();
                    if (user.isEmailVerified()) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, new ArrayList());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        return authentication;
                    }
                }
            } catch (JWTDecodeException ex) {
            }
        }

        SecurityContextHolder.getContext().setAuthentication(null);
        System.out.println("Security Context is null");
        return null;
    }

}
