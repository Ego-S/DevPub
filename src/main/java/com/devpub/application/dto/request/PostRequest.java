package com.devpub.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostRequest {
	private long timestamp;
	private byte active;
	private String title;
	private List<String> tags;
	private String text;
}
