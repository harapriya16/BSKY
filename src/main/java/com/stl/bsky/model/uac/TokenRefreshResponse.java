package com.stl.bsky.model.uac;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponse {

	private String accessToken;
	  private String refreshToken;
	  private String tokenType = "Bearer";

}
