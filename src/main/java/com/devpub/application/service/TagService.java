package com.devpub.application.service;

import com.devpub.application.model.Tag;
import com.devpub.application.model.TagToPost;
import com.devpub.application.repository.TagRepository;
import com.devpub.application.repository.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService {
	private final TagRepository tagRepository;
	private final TagToPostRepository tagToPostRepository;

	@Autowired
	public TagService(
			TagRepository tagRepository,
			TagToPostRepository tagToPostRepository
	) {
		this.tagRepository = tagRepository;
		this.tagToPostRepository = tagToPostRepository;
	}

	public int getIdByName(String tagName) {
		return tagRepository.getIdByName(tagName);
	}

	public List<String> findTagsForPost(int postId) {
		return tagRepository.findTagsForPost(postId);
	}

	public void saveTag(String tagName, int postId) {
		tagName = tagName.toUpperCase().replaceAll("[^(0-9A-ZА-ЯЁ\\s)]", "_");
		Tag tag = tagRepository.getTagByName(tagName);

		if (tag == null) {
			tag = new Tag();
			tag.setName(tagName);
			tagRepository.save(tag);
		}

		TagToPost tagToPost = tagToPostRepository.findByPostIdAndTagId(postId, tag.getId());

		if (tagToPost == null) {
			tagToPost = new TagToPost();
			tagToPost.setPostId(postId);
			tagToPost.setTagId(tag.getId());
			tagToPostRepository.save(tagToPost);
		}
	}
}
