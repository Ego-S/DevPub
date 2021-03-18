package com.devpub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForCommentDTO {
	private int id;
	private String name;
	private String photo;
}
