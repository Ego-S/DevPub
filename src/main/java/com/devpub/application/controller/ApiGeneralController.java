package com.devpub.application.controller;

import com.devpub.application.dto.response.SettingsDTO;
import com.devpub.application.dto.response.InitResponse;
import com.devpub.application.dto.response.TagDTO;
import com.devpub.application.dto.response.TagsDTO;
import com.devpub.application.service.SettingsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
	private ResponseEntity<TagsDTO> getTags() {
		List<TagDTO> tags = new ArrayList<>();
		tags.add(new TagDTO("Test", 1.0));
		TagsDTO body = new TagsDTO(tags);
		return new ResponseEntity<>(body, HttpStatus.OK);
	}

}
