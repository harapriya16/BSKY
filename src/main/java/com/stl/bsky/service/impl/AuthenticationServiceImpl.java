package com.stl.bsky.service.impl;

import com.stl.bsky.common.CaptchaModel;
import com.stl.bsky.common.Sha512;
import com.stl.bsky.common.Utils;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.repository.uac.UserRepository;
import com.stl.bsky.service.AuthenticationService;

import lombok.extern.slf4j.Slf4j;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    Sha512 sha512;

    @Override
    public UserMaster postUserCredentials(String username, String password, String salt) {
        System.out.println(username);
        UserMaster user = userRepository.findByUserName(username);
        

        //   if (user == null)throw new BadCredentialsException("Invalid username and password");
        if (user == null) {
            return null;
        }

        if (user.getStatus().getStatusId() != 1) throw new BadCredentialsException("User not active");
        String userPassword = null;

        userPassword = user.getPassword();

        Sha512 sha15 = new Sha512();
        String saltPassword = sha15.SHA512(userPassword + "#" + salt);

        if (!password.equals(saltPassword)) {
            return user = null;
        }

        System.out.println(password + " ==user obj== " + user.toString());

        return user;
    }

	@Override
	public Boolean checkCaptchaValidity(HttpServletRequest req) {
		Boolean valid=true;
		CaptchaModel cm= Utils.setCaptchaModelClassfromString(Sha512.decrypt(req.getHeader("captchaKey")));
		if(!cm.getHostAddr().equalsIgnoreCase(req.getRemoteAddr())) {
			valid=false;
		}else {
			if(new Date().getTime()>cm.getExpireTime()) {
				valid=false;
			}
		}
		return valid;
	}
}
