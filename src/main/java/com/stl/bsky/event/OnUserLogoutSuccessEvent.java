package com.stl.bsky.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import com.stl.bsky.model.uac.LogoutRequest;

import java.time.Instant;
import java.util.Date;

@Getter
@Setter
public class OnUserLogoutSuccessEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;
    private final String userEmail;
    private final String token;
    private final transient LogoutRequest logoutRequest;
    private final Date eventTime;

    public OnUserLogoutSuccessEvent(String userName, String token, LogoutRequest logoutRequest) {
        super(userName);
        this.userEmail = userName;
        this.token = token;
        this.logoutRequest = logoutRequest;
        this.eventTime = Date.from(Instant.now());
    }
}
