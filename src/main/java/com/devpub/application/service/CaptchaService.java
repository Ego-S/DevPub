package com.devpub.application.service;

import com.devpub.application.dto.response.CaptchaResponse;
import com.devpub.application.model.Captcha;
import com.devpub.application.repository.CaptchaRepository;
import com.github.cage.Cage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class CaptchaService {
	@Value("${captchaMaxLength}")
	public int captchaMaxLength;
	@Value("${captchaLifeTimeInMinutes}")
	public int captchaLifeTimeInMinutes;

	private final CaptchaRepository captchaRepository;
	private final String IMAGE_PREFIX = "data:image/png;base64, ";

	@Autowired
	private PasswordEncoder encoder;
	@Autowired
	private Cage cage;

	@Autowired
	public CaptchaService (
			CaptchaRepository captchaRepository
	) {
		this.captchaRepository = captchaRepository;
	}

	public Optional<Captcha> findCaptchaByCodeAndSecretCode(String code, String secretCode) {
		return captchaRepository.findByCodeAndSecretCode(code, secretCode);
	}

	public CaptchaResponse captcha() {
		//generate captcha code, secret code and image
		Random random = new Random();
		String code = Long.toString(Math.abs(random.nextLong()), 32);
		code = code.substring(0, Math.min(code.length(), captchaMaxLength));
		String secretCode = encoder.encode(code);
		String image = Base64.getEncoder().encodeToString(cage.draw(code));

		//save and delete old
		saveCaptcha(code, secretCode);
		deleteOldCaptcha();

		//return
		return new CaptchaResponse(secretCode, IMAGE_PREFIX + image);
	}

	private void deleteOldCaptcha() {
		captchaRepository.deleteAll(new ArrayList<>(
				captchaRepository.findByTimeLessThen(LocalDateTime.now().minusMinutes(captchaLifeTimeInMinutes))
		));
	}

	private void saveCaptcha(String code, String secretCode) {
		Captcha captcha = new Captcha();
		captcha.setCode(code);
		captcha.setSecretCode(secretCode);
		captcha.setTime(LocalDateTime.now());
		captchaRepository.save(captcha);
	}
}
