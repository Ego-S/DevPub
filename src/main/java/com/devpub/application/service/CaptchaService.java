package com.devpub.application.service;

import com.devpub.application.model.Captcha;
import com.devpub.application.repository.CaptchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CaptchaService {

	private final CaptchaRepository captchaRepository;

	@Autowired
	public CaptchaService (
			CaptchaRepository captchaRepository
	) {
		this.captchaRepository = captchaRepository;
	}

	public Optional<Captcha> findCaptchaByCodeAndSecretCode(String code, String secretCode) {
		return captchaRepository.findByCodeAndSecretCode(code, secretCode);
	}
}
