package com.stl.bsky.model.uac;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutRequest {
    private String userName;
    private String token;
}
