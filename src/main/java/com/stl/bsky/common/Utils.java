package com.stl.bsky.common;

public class Utils {
	
	public static String getCaptchaKeyFromResponse(String response){
		String res="";
		if(response.indexOf("captchaKey=")!=-1) {
			res=response.substring(response.indexOf("captchaKey=")+"captchaKey=".length(), response.indexOf(", expireTime"));
		}
		return res;
	}
	public static String getCaptchaValidityFromResponse(String response){
		String res="";
		if(response.indexOf("expireTime=")!=-1) {
			res=response.substring(response.indexOf("expireTime=")+"expireTime=".length(), response.indexOf(", hostAddr"));
		}
		return res;
	}
	public static String getHostAddrFromResponse(String response){
		String res="";
		if(response.indexOf("hostAddr=")!=-1) {
			res=response.substring(response.indexOf("hostAddr=")+"hostAddr=".length(), response.indexOf(")"));
		}
		return res;
	}
	
	public static CaptchaModel setCaptchaModelClassfromString(String str) {
		CaptchaModel cm= new CaptchaModel();
		cm.setCaptchaKey(getCaptchaKeyFromResponse(str));
		cm.setExpireTime(Long.parseLong(getCaptchaValidityFromResponse(str)));
		cm.setHostAddr(getHostAddrFromResponse(str));
		return cm;
	}

}
