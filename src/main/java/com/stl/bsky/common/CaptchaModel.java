package com.stl.bsky.common;

import lombok.Data;

@Data
public class CaptchaModel {
	
	private String captchaKey;
	
	private Long expireTime;
	
	private String hostAddr;

}
