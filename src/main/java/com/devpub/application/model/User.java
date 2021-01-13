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
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "is_moderator", nullable = false)
	private boolean isModerator;

	@Column(name = "reg_time", nullable = false)
	private LocalDateTime registrationTime;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	private String code;

	@Column(name = "photo", columnDefinition = "TEXT")
	private String photoPath;
}
