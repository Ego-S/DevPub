package com.devpub.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
	private int id;
	private long timestamp;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("active")
	private Boolean isActive;
	private UserForPostDTO user;
	private String title;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String announce;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String text;
	private int likeCount;
	private int dislikeCount;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer commentCount;
	private int viewCount;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<CommentDTO> comments;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> tags;
}
