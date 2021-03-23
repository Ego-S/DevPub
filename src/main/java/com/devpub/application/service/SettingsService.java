package com.devpub.application.service;

import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.dto.response.SettingsDTO;
import com.devpub.application.enums.GlobalSettingCode;
import com.devpub.application.enums.GlobalSettingValue;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.model.GlobalSetting;
import com.devpub.application.repository.SettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
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
			boolean value = s.getValue().equals(GlobalSettingValue.YES);
			settings.put(s.getCode().toString(), value);
		});

		return new SettingsDTO(
				settings.get(GlobalSettingCode.MULTIUSER_MODE.toString()),
				settings.get(GlobalSettingCode.POST_PREMODERATION.toString()),
				settings.get(GlobalSettingCode.STATISTICS_IS_PUBLIC.toString())
		);
	}

	public ResultDTO putSettings(SettingsDTO settingsDTO) {
		GlobalSetting multiUserMode = settingsRepository.findByCode(GlobalSettingCode.MULTIUSER_MODE);
		GlobalSettingValue multiUserModeValue
				= settingsDTO.isMultiuserMode() ? GlobalSettingValue.YES : GlobalSettingValue.NO;
		multiUserMode.setValue(multiUserModeValue);
		settingsRepository.save(multiUserMode);

		GlobalSetting postPreModeration = settingsRepository.findByCode(GlobalSettingCode.POST_PREMODERATION);
		GlobalSettingValue postPreModerationValue
				= settingsDTO.isPostPremoderation() ? GlobalSettingValue.YES : GlobalSettingValue.NO;
		postPreModeration.setValue(postPreModerationValue);
		settingsRepository.save(postPreModeration);

		GlobalSetting statisticIsPublic = settingsRepository.findByCode(GlobalSettingCode.STATISTICS_IS_PUBLIC);
		GlobalSettingValue statisticIsPublicValue
				= settingsDTO.isStatisticPublic() ? GlobalSettingValue.YES : GlobalSettingValue.NO;
		multiUserMode.setValue(statisticIsPublicValue);
		settingsRepository.save(statisticIsPublic);

		return new ResultDTO(true, null);
	}

	public ModerationStatus getStatusForNewPost() {
		GlobalSetting postPreModeration = settingsRepository.findByCode(GlobalSettingCode.POST_PREMODERATION);
		return postPreModeration.getValue().equals(GlobalSettingValue.YES) ?
				ModerationStatus.NEW : ModerationStatus.ACCEPTED;
	}
}
