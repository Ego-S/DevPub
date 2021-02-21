package com.devpub.application.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {
	USER("user"),
	MODERATE("moderator");

	private final String permission;
}
