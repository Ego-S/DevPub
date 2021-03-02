package com.devpub.application.repository;

import com.devpub.application.model.Captcha;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaptchaRepository extends CrudRepository<Captcha, Integer> {
	Optional<Captcha> findByCodeAndSecretCode(String code, String secretCode);

	@Query("FROM Captcha c WHERE c.time < :time")
	List<Captcha> findByTimeLessThen(LocalDateTime time);
}
