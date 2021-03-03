package com.devpub.application.service;

import com.devpub.application.dto.response.SettingsDTO;
import com.devpub.application.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsService {
	private final SettingsRepository settingsRepository;

	@Autowired
	public SettingsService(
			SettingsRepository settingsRepository
	) {
		this.settingsRepository = settingsRepository;
	}

	public SettingsDTO getSettings() {
		Map<String, Boolean> settings = new HashMap<>();
		settingsRepository.findAll().forEach(s -> {
			boolean value = s.getValue().equals("YES");
			settings.put(s.getCode(), value);
		});

		return new SettingsDTO(
				settings.get("MULTIUSER_MODE"),
				settings.get("POST_PREMODERATION"),
				settings.get("STATISTICS_IS_PUBLIC")
		);
	}
}
