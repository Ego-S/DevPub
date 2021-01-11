package com.devpub.application.model;

import com.devpub.application.enums.ModerationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@ToString

@Entity
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "is_active", nullable = false)
	private boolean isActive;

	@Enumerated(EnumType.STRING)
	@Column(name = "moderation_status", nullable = false)
	private ModerationStatus moderationStatus;

	@Column(name = "moderator_id")
	private int moderatorId;

	@Column(name = "user_id", nullable = false)
	private int userId;

	@Column(name = "time", nullable = false)
	private LocalDateTime postTime;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;

	@Column(name = "view_count", nullable = false)
	private int viewCount;

}
