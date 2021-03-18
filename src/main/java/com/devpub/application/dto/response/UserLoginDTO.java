package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
	private int id;
	private String name;
	private String photo;
	private String email;
	@JsonProperty("moderation")
	private boolean isModerator;
	private int moderationCount;
	@JsonProperty("settings")
	private boolean hasAccessToSettings;
}
