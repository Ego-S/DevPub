package com.devpub.application.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DefaultController {
	@RequestMapping("/")
	public String index() {
		return "index";
	}

	@RequestMapping("posts/recent")
	public String recent() {
		return "index";
	}

	@RequestMapping("posts/popular")
	public String popular() {
		return "index";
	}

	@RequestMapping("posts/best")
	public String best() {
		return "index";
	}

	@RequestMapping("posts/early")
	public String early() {
		return "index";
	}
	
}
