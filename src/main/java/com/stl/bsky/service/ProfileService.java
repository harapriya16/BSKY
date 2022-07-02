package com.stl.bsky.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.stl.bsky.entity.uac.UserMaster;

public interface ProfileService {

	ResponseEntity<Object> updateProfileDetails(UserMaster userMaster, HttpServletRequest request);
	
}
