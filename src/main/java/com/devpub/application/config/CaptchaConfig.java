package com.devpub.application.config;

import com.github.cage.Cage;
import com.github.cage.image.EffectConfig;
import com.github.cage.image.Painter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class CaptchaConfig {

	@Value("${captchaImgWidth}")
	private int captchaImgWidth;
	@Value("${captchaImgHeight}")
	private int captchaImgHeight;

	@Bean
	public Cage cageBean() {
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
