package com.devpub.application.service;

import com.devpub.application.dto.response.TagDTO;
import com.devpub.application.dto.response.TagsDTO;
import com.devpub.application.model.Tag;
import com.devpub.application.model.TagToPost;
import com.devpub.application.repository.TagRepository;
import com.devpub.application.repository.TagToPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

	public TagsDTO getTags(String query) {
		//get all tags by query
		if (query == null) {
			query = "";
		}
		query = query + "%";
		List<Tag> tagList = tagRepository.findByNameLike(query);

		//get TagDTO list (tags with no normalize weight)
		long allPostCount = tagRepository.getAllPostCount();
		double maxWeight = 0;

		List<TagDTO> tagDTOList = new ArrayList<>();

		for (Tag tag : tagList) {
			int postCountByTag = tagToPostRepository.countByTagId(tag.getId());
			double weight = (double) postCountByTag / allPostCount;

			TagDTO tagDTO = new TagDTO(tag.getName(), weight);
			tagDTOList.add(tagDTO);
			//looking max value tag weight
			maxWeight = Math.max(maxWeight, weight);
		}

		//normalize weight
		double normalizeCoefficient = 1 / maxWeight;

		for (TagDTO tagDTO : tagDTOList) {
			double normalizedWeight = tagDTO.getWeight() * normalizeCoefficient;
			tagDTO.setWeight(normalizedWeight);
		}
		//return
		return new TagsDTO(tagDTOList);
	}
}
