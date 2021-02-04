package com.devpub.application.controller;

import com.devpub.application.dto.SettingsDTO;
import com.devpub.application.dto.InitResponse;
import com.devpub.application.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

	private final InitResponse initResponse;
	private final SettingsService settingsService;

	public ApiGeneralController(InitResponse initResponse, SettingsService settingsService) {
		this.initResponse = initResponse;
		this.settingsService = settingsService;
	}

	@GetMapping("/init")
	private ResponseEntity<InitResponse> init() {
		return new ResponseEntity<>(initResponse, HttpStatus.OK);
	}

	@GetMapping("/settings")
	private ResponseEntity<SettingsDTO> getGlobalSettings() {
		return new ResponseEntity<>(settingsService.getSettings(), HttpStatus.OK);
	}

	//TODO
	//ЗАГЛУШКА
	@GetMapping("/tag")
	private ResponseEntity<String> getTags() {
		String body = "{\"tags\": [{\"name\": \"test\", \"weight\": 1}]}";
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

}
