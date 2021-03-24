package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDTO {
	private boolean result;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private UserLoginDTO user;
}
