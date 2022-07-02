package com.stl.bsky.model.uac;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class TokenRefreshRequest {
	@NotBlank
	private String refreshToken;
}
