package com.devpub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDTO {
	private int id;
	private long timestamp;
	private String text;
	private UserForCommentDTO user;
}
