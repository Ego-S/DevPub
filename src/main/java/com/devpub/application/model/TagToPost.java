package com.devpub.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "tag2post")
public class TagToPost {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "post_id", nullable = false)
	private int postId;

	@Column(name = "tag_id", nullable = false)
	private int tagId;
}
