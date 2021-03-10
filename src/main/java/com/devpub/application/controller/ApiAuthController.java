package com.devpub.application.controller;

import com.devpub.application.dto.request.ChangePasswordRequest;
import com.devpub.application.dto.request.LoginRequest;
import com.devpub.application.dto.request.RegistrationBody;
import com.devpub.application.dto.request.UserEmailRequest;
import com.devpub.application.dto.response.CaptchaResponse;
import com.devpub.application.dto.response.LoginDTO;
import com.devpub.application.dto.response.LogoutResponse;
import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.service.CaptchaService;
import com.devpub.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

	private final UserService userService;
	private final CaptchaService captchaService;

	@Autowired
	public ApiAuthController(
			UserService userService,
			CaptchaService captchaService
	) {
		this.userService = userService;
		this.captchaService = captchaService;
	}

	@GetMapping("/check")
	public ResponseEntity<LoginDTO> check(Principal principal) {
		return principal == null ?
				ResponseEntity.ok(new LoginDTO(false, null)) :
				ResponseEntity.ok(userService.getLoginDTO(principal.getName()));
	}

	@PostMapping("/login")
	public ResponseEntity<LoginDTO> login(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.login(loginRequest));
	}

	@GetMapping("/logoutSuccess")
	public ResponseEntity<LogoutResponse> logout() {
		return ResponseEntity.ok(new LogoutResponse(true));
	}

	@PostMapping("/register")
	public ResponseEntity<ResultDTO> registration(@RequestBody RegistrationBody registrationBody) {
		return ResponseEntity.ok(userService.registration(registrationBody));
	}

	@GetMapping("/captcha")
	public ResponseEntity<CaptchaResponse> captcha() {
		return ResponseEntity.ok(captchaService.captcha());
	}

	@PostMapping("/restore")
	public ResponseEntity<ResultDTO> restore(@RequestBody UserEmailRequest userEmailBody) {
		return ResponseEntity.ok(userService.restore(userEmailBody));
	}

	@PostMapping("/password")
	public ResponseEntity<ResultDTO> changePassword(@RequestBody ChangePasswordRequest request) {
		return ResponseEntity.ok(userService.changePassword(request));
	}
}
