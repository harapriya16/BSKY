package com.stl.bsky.model.registration;

import lombok.Data;

@Data
public class GeneratePaswd {
    private String verificationCode;
    private String encodedPassword;
}
