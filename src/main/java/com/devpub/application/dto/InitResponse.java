package com.devpub.application.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class InitResponse {
	@Value("${title}")
	private String title;
	@Value("${subtitle}")
	private String subtitle;
	@Value("${phone}")
	private String phone;
	@Value("${email}")
	private String email;
	@Value("${copyright}")
	private String copyright;
	@Value("${copyrightFrom}")
	private String copyrightFrom;
}
