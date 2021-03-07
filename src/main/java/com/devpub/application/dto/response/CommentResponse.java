package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class CommentResponse {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	Integer id;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	Boolean result;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	Map<String, String> errors;
}
