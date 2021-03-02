package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponse {
	@JsonProperty("secret")
	private String secretCode;
	private String image;
}
