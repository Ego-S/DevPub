package com.devpub.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticDTO {
	private long postsCount;
	private long likesCount;
	private long dislikesCount;
	private long viewsCount;
	private Long firstPublication;
}
