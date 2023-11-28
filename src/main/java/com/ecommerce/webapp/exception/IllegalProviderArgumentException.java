package com.ecommerce.webapp.exception;

public class IllegalProviderArgumentException extends RuntimeException {

    private String provider;

    public IllegalProviderArgumentException(String message, String provider) {
        super(message);
        this.provider = provider;
    }

    public String getProvider() {
        return provider;
    }
}
