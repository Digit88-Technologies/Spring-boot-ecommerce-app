package com.ecommerce.webapp.api.model;

import java.time.LocalDateTime;

/**
 * Class For Custom Error Response
 */
public class CustomErrorResponse {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;

    public CustomErrorResponse(int statusCode, String message, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
