package com.devpub.application.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "captcha_codes")
public class Captcha {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private LocalDateTime time;

	@Column(nullable = false, columnDefinition = "TINYTEXT")
	private String code;

	@Column(name = "secret_code", nullable = false, columnDefinition = "TINYTEXT")
	private String secretCode;
}
