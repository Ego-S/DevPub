package com.devpub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticDTO {
	private long postCount;
	private long likesCount;
	private long dislikeCount;
	private long viewsCount;
	private Long firstPublication;
}
