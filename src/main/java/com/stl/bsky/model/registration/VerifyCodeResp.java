package com.stl.bsky.model.registration;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VerifyCodeResp {
    private int testingAgency;
    private String userid;
    private String emailId;
    private int status;
}
