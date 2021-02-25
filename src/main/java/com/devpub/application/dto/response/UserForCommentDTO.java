package com.devpub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserForCommentDTO {
	private int id;
	private String name;
	private String photo;
}
