package com.devpub.application.controller;

import com.devpub.application.dto.UserAuthDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

	//TODO
	//ЗАГЛУШКА
	@GetMapping("/check")
	public ResponseEntity<UserAuthDTO> check() {
		return new ResponseEntity<>(new UserAuthDTO(false), HttpStatus.OK);
	}
}
