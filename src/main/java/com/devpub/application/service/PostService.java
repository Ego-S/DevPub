package com.devpub.application.service;

import com.devpub.application.dto.PostDTO;
import com.devpub.application.dto.PostPageDTO;
import com.devpub.application.dto.UserForPostDTO;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.TagRepository;
import com.devpub.application.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class PostService {

	private final PostRepository postRepository;
	private final TagRepository tagRepository;
	private final UserRepository userRepository;

	@Value("${announceLength}")
	private int announceLength;


	@Autowired
	public PostService (PostRepository postRepository, TagRepository tagRepository, UserRepository userRepository) {
		this.postRepository = postRepository;
		this.tagRepository = tagRepository;
		this.userRepository = userRepository;
	}

	public PostPageDTO getPostPage(int offset, int limit, String mode) {
		Page<Post> postPage;
		Sort sort;
		Pageable pageable;
		int page = offset / limit;

		switch(mode) {
			case "best" :
				pageable = PageRequest.of(page, limit);
				postPage = postRepository.findAllBestAcceptedPostsBefore(true, ModerationStatus.ACCEPTED.toString(), LocalDateTime.now() ,pageable);
				return postPageToPostPageDTO(postPage);
			case "popular" :
				sort = JpaSort.unsafe(Sort.Direction.DESC, "size(p.comments)");
				break;
			case "early" :
				sort = Sort.by("postTime").ascending();
				break;
			default:
				sort = Sort.by("postTime").descending();
				break;
		}
		pageable = PageRequest.of(page, limit, sort);
		postPage = postRepository.findAll(true, ModerationStatus.ACCEPTED,LocalDateTime.now() ,pageable);

		return postPageToPostPageDTO(postPage);
	}

	public PostPageDTO getPostsPageLike(int offset, int limit, String query) {
		int pageNumber = offset / limit;
		Page<Post> page;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		if (query.length() == 0) {
			page = postRepository.findAll(true, ModerationStatus.ACCEPTED, LocalDateTime.now(), pageable);
		} else {
			query = "%" + query + "%";
			page = postRepository.search(true, ModerationStatus.ACCEPTED, LocalDateTime.now(), query, pageable);
		}

		return postPageToPostPageDTO(page);
	}

	public PostPageDTO getPostsPageByDate(int offset, int limit, LocalDate date) {
		int pageNumber = offset / limit;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		LocalDateTime from = date.atTime(LocalTime.MIN);
		LocalDateTime to = date.atTime(LocalTime.MAX);

		Page<Post> page = postRepository.findAllByDate(true, ModerationStatus.ACCEPTED, LocalDateTime.now(), from, to, pageable);

		return postPageToPostPageDTO(page);
	}

	public PostPageDTO getPostsPageByTag(int offset, int limit, String tag) {
		int pageNumber = offset / limit;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		int tagId = tagRepository.getIdByName(tag);
		Page<Post> page = postRepository.findAllByTag(true, ModerationStatus.ACCEPTED, LocalDateTime.now(), tagId, pageable);

		return postPageToPostPageDTO(page);
	}

	public PostPageDTO myPosts(int offset, int limit, String status, Principal principal) {
		String email = principal.getName();
		User user = userRepository.findByEmail(email)
						.orElseThrow(() -> new UsernameNotFoundException(email));

		Page<Post> postPage;
		Sort sort = Sort.by("postTime").descending();
		int page = offset / limit;
		Pageable pageable = PageRequest.of(page, limit, sort);

		switch (status) {
			case "inactive" :
				postPage = postRepository.findMyPostWithAnyStatus(user, false, pageable);
				break;
			case "pending" :
				postPage = postRepository.findMyPosts(user, true, ModerationStatus.NEW, pageable);
				break;
			case "declined" :
				postPage = postRepository.findMyPosts(user, true, ModerationStatus.DECLINED, pageable);
				break;
			case "published" :
				postPage = postRepository.findMyPosts(user, true, ModerationStatus.ACCEPTED, pageable);
				break;
			default: return new PostPageDTO();
		}

		return postPageToPostPageDTO(postPage);
	}

	private UserForPostDTO userToUserForPostDTO(User user) {
		return new UserForPostDTO(user.getId(), user.getName());
	}

	private String getAnnounceFromText(String text) {
		String clearText = text.replaceAll("(<\\S+>)", "");
		return clearText.substring(0, Math.min(clearText.length(), announceLength));
	}

	private PostPageDTO postPageToPostPageDTO(Page<Post> postPage) {
		List<PostDTO> postDTOList = new ArrayList<>();
		postPage.getContent().forEach(post -> {
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
		int count = (int) postPage.getTotalElements();
		return new PostPageDTO(count, postDTOList);
	}


}