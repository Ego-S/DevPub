package com.devpub.application.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationBody {
	@JsonProperty("e_mail")
	private String email;
	private String password;
	private String name;
	private String captcha;
	@JsonProperty("captcha_secret")
	private String captchaSecret;
}
