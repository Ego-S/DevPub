package com.devpub.application.service;

import com.devpub.application.dto.response.CaptchaResponse;
import com.devpub.application.model.Captcha;
import com.devpub.application.repository.CaptchaRepository;
import com.github.cage.Cage;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@Service
public class CaptchaService {

	@Value("${captchaImgWidth}")
	private int captchaImgWidth;
	@Value("${captchaImgHeight}")
	private int captchaImgHeight;
	@Value("${captchaMaxLength}")
	private int captchaMaxLength;
	@Value("${captchaLifeTimeInMinutes}")
	private int captchaLifeTimeInMinutes;

	private final CaptchaRepository captchaRepository;
	private final String IMAGE_PREFIX = "data:image/png;base64, ";

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	public CaptchaService (
			CaptchaRepository captchaRepository
	) {
		this.captchaRepository = captchaRepository;
	}

	public Optional<Captcha> findCaptchaByCodeAndSecretCode(String code, String secretCode) {
		return captchaRepository.findByCodeAndSecretCode(code, secretCode);
	}

	public ResponseEntity<CaptchaResponse> captcha() {
		//generate captcha code, secret code and image
		Random random = new Random();
		String code = Long.toString(Math.abs(random.nextLong()), 32);
		code = code.substring(0, Math.min(code.length(), captchaMaxLength));
		String secretCode = encoder.encode(code);
		String image = Base64.getEncoder().encodeToString(cageBean().draw(code));

		//save and delete old
		saveCaptcha(code, secretCode);
		deleteOldCaptcha();

		//return
		CaptchaResponse captchaResponse = new CaptchaResponse(secretCode, IMAGE_PREFIX + image);
		return ResponseEntity.ok(captchaResponse);
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

	@Bean
	private Cage cageBean() {
		return new Cage(
				new Painter(
						captchaImgWidth,
						captchaImgHeight,
						null,
						null,
						new EffectConfig(
								true,
								true,
								false,
								true,
								null),
						new Random()),
				null,
				null,
				null,
				1.0f,
				null,
				new Random()
		);
	}
}
