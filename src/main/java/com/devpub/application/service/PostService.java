package com.devpub.application.service;

import com.devpub.application.dto.PostDTO;
import com.devpub.application.dto.PostPageDTO;
import com.devpub.application.dto.UserForPostDTO;
import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.repository.PostRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class PostService {

	private final PostRepository postRepository;

	@Value("${announceLength}")
	private int announceLength;


	@Autowired
	public PostService (PostRepository postRepository) {
		this.postRepository = postRepository;
	}

	public PostPageDTO getPostsPage(int offset, int limit, String mode) {
		int pageNumber = offset / limit;
		Page<Post> page;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		switch (mode) {
			case "recent" :
				page = postRepository.findAllAcceptedPostsBeforeSortedByTimeDesc(LocalDateTime.now(), pageable);
				break;
			case "early" :
				page = postRepository.findAllAcceptedPostsBeforeSortedByTimeAsc(LocalDateTime.now(), pageable);
				break;
			case "popular" :
				page = postRepository.findAllPopularAcceptedPostsBefore(LocalDateTime.now(), pageable);
				break;
			case "best" :
				page = postRepository.findAllBestAcceptedPostsBefore(LocalDateTime.now(), pageable);
				break;
			default: page = postRepository.findAllAcceptedPostsBefore(LocalDateTime.now(), pageable);
				break;
		}

		int count = (int) page.getTotalElements();
		PostPageDTO posts = postListToPostDTOList(count, page.getContent());
		return posts;
	}

	private PostPageDTO postListToPostDTOList(int count, List<Post> postList) {
		PostPageDTO postPageDTO = new PostPageDTO();
		List<PostDTO> postDTOList = new ArrayList<>();
		postList.forEach(post -> {
			PostDTO postDTO = new PostDTO();
			postDTO.setId(post.getId());
			postDTO.setTimestamp(Timestamp.valueOf(post.getPostTime()).getTime()/1000);
			postDTO.setUser(userToUserForPostDTO(post.getUser()));
			postDTO.setTitle(post.getTitle());
			postDTO.setAnnounce(getAnnounceFromText(post.getText()));
			postDTO.setLikeCount(postRepository.likesCountOnPost(post));
			postDTO.setDislikeCount(postRepository.dislikesCountOnPost(post));
			postDTO.setCommentCount(postRepository.commentCountByPost(post.getId()));
			postDTO.setViewCount(post.getViewCount());
			postDTOList.add(postDTO);
		});
		postPageDTO.setCount(count);
		postPageDTO.setPosts(postDTOList);
		System.out.println("count of elements in postPageDTO - " + postPageDTO.getCount());
		return postPageDTO;
	}

	private UserForPostDTO userToUserForPostDTO(User user) {
		return new UserForPostDTO(user.getId(), user.getName());
	}


	private String getAnnounceFromText(String text) {
		String clearText = text.replaceAll("(<\\S+>)", "");
		return clearText.substring(0, Math.min(clearText.length(), announceLength));
	}
}
