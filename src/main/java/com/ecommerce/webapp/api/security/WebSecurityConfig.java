package com.ecommerce.webapp.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
public class WebSecurityConfig {

  private JWTRequestFilter jwtRequestFilter;

  public WebSecurityConfig(JWTRequestFilter jwtRequestFilter) {
    this.jwtRequestFilter = jwtRequestFilter;
  }

  /**
   * Filter chain to configure security.
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
        .requestMatchers("/product", "/auth/register", "/auth/login",
            "/auth/verify", "/auth/forgot", "/auth/reset", "/auth/validateOTP","/auth/sendOTP","/elastic/**").permitAll()
        .anyRequest().authenticated();
    return http.build();
  }

}
