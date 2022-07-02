package com.stl.bsky.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stl.bsky.common.ImageUpload;
import com.stl.bsky.common.StatusResponse;
import com.stl.bsky.entity.uac.StatusMaster;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.repository.uac.StatusRepository;
import com.stl.bsky.repository.uac.UserRepository;
import com.stl.bsky.service.CommonService;
import com.stl.bsky.service.ProfileService;

@Service
public class ProfileServiceImpl implements ProfileService{
	@Autowired
    CommonService commonService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	StatusRepository statusRepository;

	@Override
	public ResponseEntity<Object> updateProfileDetails(UserMaster userMaster, HttpServletRequest request) {
		UserMaster umaster = commonService.extractUserFromToken(request);		
		if(userMaster.getId().equals(umaster.getId())) {	
			String fileName=commonService.checkAndSaveAttachmentInUserMaster(userMaster.getProfilePic());			
			userRepository.updateProfileDetails(userMaster.getFirstName(),userMaster.getLastName(), fileName, userMaster.getId());			
			return new ResponseEntity<>(new StatusResponse(1, "User updated successfully"), HttpStatus.OK);
		}else {
			return new ResponseEntity<>(new StatusResponse(0, "User details not updated or Some exception occurred"),
                    HttpStatus.OK);
		}
	}
}
