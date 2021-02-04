package com.devpub.application.service;

import com.devpub.application.dto.SettingsDTO;
import com.devpub.application.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SettingsService {
	private final SettingsRepository settingsRepository;

	@Autowired
	public SettingsService(SettingsRepository settingsRepository) {
		this.settingsRepository = settingsRepository;
	}

	public SettingsDTO getSettings() {
		boolean multiuserMode = settingsRepository.getMultiUserSettingValue().equals("YES");
		boolean postPremoderation = settingsRepository.getPostPremoderationSettingValue().equals("YES");
		boolean isStatisticIsPublic = settingsRepository.getIsStatisticPublicSettingValue().equals("YES");
		return new SettingsDTO(multiuserMode, postPremoderation, isStatisticIsPublic);
	}
}
