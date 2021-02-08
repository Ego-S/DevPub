package com.devpub.application.repository;

import com.devpub.application.model.GlobalSetting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {
}
