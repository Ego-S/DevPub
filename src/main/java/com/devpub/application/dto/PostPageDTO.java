package com.devpub.application.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostPageDTO {
	private int count;
	private List<PostDTO> posts;
}
