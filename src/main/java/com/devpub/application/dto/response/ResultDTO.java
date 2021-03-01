package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultDTO {
	private boolean result;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Map<String, String> errors;
}
