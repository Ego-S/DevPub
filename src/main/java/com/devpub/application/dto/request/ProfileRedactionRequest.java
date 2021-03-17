package com.devpub.application.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ProfileRedactionRequest {
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String name;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String email;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String password;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Boolean removePhoto;
}
