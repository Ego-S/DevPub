package com.devpub.application.controller;

import com.devpub.application.dto.LoginDTO;
import com.devpub.application.dto.LoginRequest;
import com.devpub.application.dto.LogoutResponse;
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


}
