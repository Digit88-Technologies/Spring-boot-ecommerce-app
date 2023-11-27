package com.ecommerce.webapp.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {

    public static final String[] STRINGS = {"/product/**", "/auth/register", "/auth/login",
            "/auth/verify", "/auth/forgot", "/auth/reset", "/auth/validateOTP", "/auth/sendOTP", "/elastic/**", "/google/**"};
    public static final String GOOGLE_LOGIN_PATH = "/google/login";
    private JWTRequestFilter jwtRequestFilter;

    public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    /**
     * Filter chain to configure security.
     *
     * @param http The security object.
     * @return The chain built.
     * @throws Exception Thrown on error configuring.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable().cors().disable();
        http.addFilterBefore(jwtRequestFilter, AuthorizationFilter.class);
        http.authorizeHttpRequests()
                // Specific exclusions or rules.
                .requestMatchers(STRINGS).permitAll()
                .anyRequest().authenticated();
        return http.build();
    }

    @Order(1) // Ensure this configuration is applied before the general configuration
    @Bean
    public SecurityFilterChain googleLoginFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .requestMatchers(GOOGLE_LOGIN_PATH).permitAll()
                .and()
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .build();


    }

}
