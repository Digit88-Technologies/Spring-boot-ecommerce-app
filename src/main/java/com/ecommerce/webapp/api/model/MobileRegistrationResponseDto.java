package com.ecommerce.webapp.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileRegistrationResponseDto {


    private OtpStatus status;
    private String message;
}
