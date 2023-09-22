package com.ecommerce.webapp.api.model;

import lombok.Data;

@Data
public class MobileRegistrationRequestDto {

    private String phoneNumber;//destination
    private String userName;
    private String oneTimePassword;
}
