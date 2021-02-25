package com.devpub.application.model;

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
@Table(name = "post_comments")
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "parent_id")
	private Integer parentId;

	@Column(name = "post_id", nullable = false)
	private int postId;

	@Column(name = "user_id", nullable = false)
	private int userId;

	@Column(nullable = false)
	private LocalDateTime time;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String text;
}
