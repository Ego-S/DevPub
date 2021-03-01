package com.devpub.application.controller;

import com.devpub.application.dto.request.RegistrationBody;
import com.devpub.application.dto.response.LoginDTO;
import com.devpub.application.dto.request.LoginRequest;
import com.devpub.application.dto.response.LogoutResponse;
import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class ApiAuthController {

	private final UserService userService;

	@Autowired
	public ApiAuthController(UserService userService) {
		this.userService = userService;
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

	@GetMapping("/logout")
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<LogoutResponse> logout() {
		return ResponseEntity.ok(userService.logout());
	}

	@PostMapping("/register")
	public ResponseEntity<ResultDTO> registration(@RequestBody RegistrationBody registrationBody) {
		return userService.registration(registrationBody);
	}


}
