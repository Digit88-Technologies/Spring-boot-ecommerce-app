package com.ecommerce.webapp.exception;

import lombok.Getter;

@Getter
public class IllegalProviderArgumentException extends RuntimeException {

    private String provider;

    public IllegalProviderArgumentException(String message, String provider) {
        super(message);
        this.provider = provider;
    }

}
