package com.devpub.application.service;

import com.devpub.application.dto.response.StatisticDTO;
import com.devpub.application.enums.GlobalSettingCode;
import com.devpub.application.enums.GlobalSettingValue;
import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.SettingsRepository;
import com.devpub.application.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class StatisticService {

	private final SettingsRepository settingsRepository;
	private final PostRepository postRepository;
	private final VoteRepository voteRepository;
	private final UserService userService;

	@Autowired
	public StatisticService(
			SettingsRepository settingsRepository,
			PostRepository postRepository,
			VoteRepository voteRepository,
			UserService userService
	) {
		this.settingsRepository = settingsRepository;
		this.postRepository = postRepository;
		this.voteRepository = voteRepository;
		this.userService = userService;
	}

	public ResponseEntity<StatisticDTO> getStatisticForBlog(Principal principal) {
		//Can we show statistic to this user? Check "statistic is public" setting and moderation status
		boolean isStatisticPublic =
				settingsRepository.findByCode(GlobalSettingCode.STATISTICS_IS_PUBLIC)
						.getValue().equals(GlobalSettingValue.YES);
		boolean isModerator = false;
		if (principal != null) {
			User user = userService.getUser(principal);
			isModerator = user.isModerator();
		}
		if (!isStatisticPublic && !isModerator) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		//create statistic
		long postCount = 0;
		long likesCount = voteRepository.countWhereValue((byte) 1);
		long dislikeCount = voteRepository.countWhereValue((byte) -1);
		long viewCount = 0;
		LocalDateTime firstPostPostTime = LocalDateTime.now();


		for (Post post : postRepository.findAll()) {
			postCount++;
			viewCount += post.getViewCount();
			if (post.getPostTime().isBefore(firstPostPostTime)) {
				firstPostPostTime = post.getPostTime();
			}
		}

		StatisticDTO statistic = new StatisticDTO(
				postCount,
				likesCount,
				dislikeCount,
				viewCount,
				Timestamp.valueOf(firstPostPostTime).getTime() / 1000
		);

		return ResponseEntity.ok(statistic);
	}
}
