package com.ecommerce.webapp.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        logIncomingRequest(request);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) throws Exception {
        logOutgoingResponse(response);
    }
    

    private void logIncomingRequest(HttpServletRequest request) {
        log.info("Incoming Request: {} {}", request.getMethod(), request.getRequestURI());
        log.debug("Request Headers: {}", request.getHeaderNames());
        log.debug("Request Parameters: {}", request.getParameterMap());
    }

    private void logOutgoingResponse(HttpServletResponse response) {
        log.info("Outgoing Response: {}", response.getStatus());
        log.debug("Response Headers: {}", response.getHeaderNames());
    }
}
