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
@Table(name = "post_votes")
public class Vote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User userId;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "post_id", nullable = false)
	private Post postId;

	@Column(nullable = false)
	private LocalDateTime time;

	@Column(nullable = false)
	private byte value;
}
