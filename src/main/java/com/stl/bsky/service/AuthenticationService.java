package com.stl.bsky.service;

import javax.servlet.http.HttpServletRequest;

import com.stl.bsky.entity.uac.UserMaster;

public interface AuthenticationService {
    UserMaster postUserCredentials(String username, String password, String captcha);

	Boolean checkCaptchaValidity(HttpServletRequest req);
}
