package com.devpub.application.controller;

import com.devpub.application.model.Blog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

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

	@GetMapping("/init")
	private Blog init(Model model) {
		return Blog.getInstance(title, subtitle, phone, email, copyright, copyrightFrom);
	}

}
