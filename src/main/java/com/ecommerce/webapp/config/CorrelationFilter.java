package com.ecommerce.webapp.config;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Component
@Data
@EqualsAndHashCode(callSuper = false)
public class CorrelationFilter extends OncePerRequestFilter {

    private final String responseHeader;
    private final String mdcKey;
    private final String requestHeader;

    public CorrelationFilter() {
        responseHeader = CorrelationFilterConfiguration.DEFAULT_HEADER_TOKEN;
        mdcKey = CorrelationFilterConfiguration.DEFAULT_MDC_UUID_TOKEN_KEY;
        requestHeader = CorrelationFilterConfiguration.DEFAULT_HEADER_TOKEN;
    }

    public CorrelationFilter(final String responseHeader, final String mdcTokenKey, final String requestHeader) {
        this.responseHeader = responseHeader;
        this.mdcKey = mdcTokenKey;
        this.requestHeader = requestHeader;
    }

        private static final String CORRELATION_ID_COOKIE_NAME = "correlationId";

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            try {
                String correlationId = extractCorrelationId(request);
                MDC.put(mdcKey, correlationId);

                if (!StringUtils.hasText(getCorrelationIdCookieValue(request))) {
                    Cookie cookie = new Cookie(CORRELATION_ID_COOKIE_NAME, correlationId);
                    cookie.setSecure(true);
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(-1); // Cookie lives until the browser is closed
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }

                filterChain.doFilter(request, response);
            } finally {
                MDC.remove(mdcKey);
            }
        }

    @Override
    protected boolean isAsyncDispatch(final HttpServletRequest request) {
        return false;
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }


    private String extractCorrelationId(HttpServletRequest request) {
        String correlationId = getCorrelationIdCookieValue(request);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString().toUpperCase().replace("-", "");
        }
        return correlationId;
    }

    private String getCorrelationIdCookieValue(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (CORRELATION_ID_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

}