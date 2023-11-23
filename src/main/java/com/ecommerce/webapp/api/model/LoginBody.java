package com.ecommerce.webapp.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The body for the login requests.
 */
public class LoginBody {

    @NotNull
    @NotBlank
    private String username;

    private String password;

    private String mobileOtp;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMobileOtp() {
        return mobileOtp;
    }

    public void setMobileOtp(String mobileOtp) {
        this.mobileOtp = mobileOtp;
    }
}
