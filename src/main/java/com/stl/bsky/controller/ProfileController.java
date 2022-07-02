package com.stl.bsky.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stl.bsky.common.Constants;
import com.stl.bsky.entity.uac.UserMaster;
import com.stl.bsky.repository.uac.UserRepository;
import com.stl.bsky.service.CommonService;
import com.stl.bsky.service.ProfileService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "profileController", description = "Profile rest controller")
@RestController
@RequestMapping(Constants.BASE_PATH + "/profiles")
@Slf4j
public class ProfileController {
	@Autowired
    CommonService commonService;
	
	@Autowired
	ProfileService profileService;
	
	@Autowired
	UserRepository userRepository;
	
	@Value("${build.base.path}")
    private String mediaBasePath;
	
	
	@GetMapping("/userProfileDetails")
    public ResponseEntity<Object> userProfileDetails(HttpServletRequest request) {
		UserMaster userMaster = commonService.extractUserFromToken(request);
//		userMaster=userRepository.findByUserName(userMaster.getUserName());
		String fullDirectory;
        String directory;
		 if (userMaster.getProfilePic() != null) {
             try {
            	 directory = File.separator + "userProfile" + File.separator;
                 fullDirectory = mediaBasePath + directory;
                 byte[] array = Files.readAllBytes(Paths.get(fullDirectory + userMaster.getProfilePic()));
                 String contentType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(array));
                 userMaster.setProfilePic("data:" + contentType + ";base64," + Base64.getEncoder().encodeToString(array));
             } catch (IOException ex) {
                 ex.printStackTrace();
             }
         }
		 System.out.println(userMaster.toString());
        return ResponseEntity.ok(userMaster);
    }
	
	@PostMapping("/updateProfileDetails")
	public ResponseEntity<Object> updateProfileDetails(@RequestBody UserMaster userMaster, HttpServletRequest request){
		System.out.println("UserMaster ++++++++++++++++++++" + userMaster.toString());
		return profileService.updateProfileDetails(userMaster,request);
	}
	
}
