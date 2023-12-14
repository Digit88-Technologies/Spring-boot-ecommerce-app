package com.ecommerce.webapp.dto;

import lombok.Data;

@Data
public class MobileOTPRequestDto {

    private String phoneNumber;//destination
    private String userName;
    private String oneTimePassword;
}
