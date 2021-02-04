package com.devpub.application.repository;

import com.devpub.application.model.GlobalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {

	@Query("SELECT gs.value FROM GlobalSetting gs WHERE code='MULTIUSER_MODE'")
	String getMultiUserSettingValue();

	@Query("SELECT gs.value FROM GlobalSetting gs WHERE code='POST_PREMODERATION'")
	String getPostPremoderationSettingValue();

	@Query("SELECT gs.value FROM GlobalSetting gs WHERE code='STATISTICS_IS_PUBLIC'")
	String getIsStatisticPublicSettingValue();
}
