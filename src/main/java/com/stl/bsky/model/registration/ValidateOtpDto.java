package com.stl.bsky.model.registration;

import lombok.Data;

@Data
public class ValidateOtpDto {
    private String emailOtp;
    private String mobileOtp;
}
