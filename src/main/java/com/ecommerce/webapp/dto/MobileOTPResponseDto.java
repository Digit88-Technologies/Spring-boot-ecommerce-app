package com.ecommerce.webapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MobileOTPResponseDto {


    private OtpStatus status;
    private String message;
}
