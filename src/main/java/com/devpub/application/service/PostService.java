package com.devpub.application.service;

import com.devpub.application.dto.request.PostRequest;
import com.devpub.application.dto.response.*;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.model.Comment;
import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.repository.CommentRepository;
import com.devpub.application.repository.PostRepository;
import com.devpub.application.repository.UserRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final CommentRepository commentRepository;

	private final TagService tagService;

	@Value("${announceLength}")
	private int announceLength;
	@Value("${titleMinLength}")
	private int titleMinLength;
	@Value("${textMinLength}")
	private int textMinLength;

	private final String TITLE_TOO_SHORT_ERROR = "Title is empty or too short.";
	private final String TEXT_TOO_SHORT_ERROR = "Text is too short.";

	@Autowired
	public PostService (
			PostRepository postRepository,
			UserRepository userRepository,
			CommentRepository commentRepository,
			TagService tagService) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.commentRepository = commentRepository;
		this.tagService = tagService;
	}

	//=============================================================================

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

		int tagId = tagService.getIdByName(tag);
		Page<Post> page = postRepository.findAllByTag(true, ModerationStatus.ACCEPTED, LocalDateTime.now(), tagId, pageable);

		return postPageToPostPageDTO(page);
	}

	public PostPageDTO myPosts(int offset, int limit, String status, Principal principal) {
		User user = getUser(principal);
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



	public PostPageDTO postsForModeration(int offset, int limit, String status, Principal principal) {
		User moderator = getUser(principal);
		Page<Post> postPage;
		Sort sort = Sort.by("postTime").descending();
		int page = offset / limit;
		Pageable pageable = PageRequest.of(page, limit, sort);

		switch (status) {
			case "new" :
				postPage = postRepository.findAllByModerationStatus(ModerationStatus.NEW, pageable);
				break;
			case "declined" :
				postPage = postRepository.findAllModeratedByMe(ModerationStatus.DECLINED, moderator, pageable);
				break;
			case "accepted" :
				postPage = postRepository.findAllModeratedByMe(ModerationStatus.ACCEPTED, moderator, pageable);
				break;
			default: return new PostPageDTO();
		}
		return postPageToPostPageDTO(postPage);
	}

	public ResponseEntity<PostDTO> getPostById(int id, Principal principal) {
		// Does it exist?
		Post post = postRepository.findPostById(id).orElse(null);
		if (post == null) {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		// Who is try to see it? Should we increment viewCount?
		User user;
		if (principal != null) {
			user = getUser(principal);
		} else {
			user = null;
		}
		// Increment viewCount if we should
		if (user == null || user != post.getUser() || !user.isModerator()) {
			post.setViewCount(post.getViewCount() + 1);
			postRepository.save(post);
		}
		return new ResponseEntity<>(postToPostDTO(post), HttpStatus.OK);
	}

	public ResponseEntity<PostPOSTResult> postPost(PostRequest postRequest, Principal principal) {
		User user = getUser(principal);

		Map<String, String> errors = new HashMap<>();

		if (postRequest.getText().length() < textMinLength) {
			errors.put("text", TEXT_TOO_SHORT_ERROR);
		}
		if (postRequest.getTitle().length() < titleMinLength) {
			errors.put("title", TITLE_TOO_SHORT_ERROR);
		}

		if (errors.size() != 0) {
			return ResponseEntity.ok(new PostPOSTResult(false, errors));
		} else {
			return ResponseEntity.ok(postPostRequest(postRequest, user));
		}
	}

	private PostPOSTResult postPostRequest(PostRequest postRequest, User user) {
		Post post = new Post();
		post.setActive(postRequest.getActive() == 1);
		post.setModerationStatus(ModerationStatus.NEW);
		post.setUser(user);
		post.setPostTime(longToLocalDateTime(Math.max(System.currentTimeMillis() / 1000, postRequest.getTimestamp())));
		post.setTitle(postRequest.getTitle());
		post.setText(postRequest.getText());

		postRepository.save(post);

		for (String tagName : postRequest.getTags()) {
			tagService.saveTag(tagName, post.getId());
		}

		return new PostPOSTResult(true, null);
	}

	//=================================================================================

	private LocalDateTime longToLocalDateTime(long sec) {
		return LocalDateTime.ofEpochSecond(sec, 0, ZoneOffset.UTC);
	}

	private User getUser(Principal principal) throws UsernameNotFoundException {
		String email = principal.getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
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
			PostDTO postDTO = postToPostDTO(post);
			// Transform postDTO format for postDTOPage
			postDTO.setAnnounce(getAnnounceFromText(post.getText()));
			postDTO.setText(null);
			postDTO.setIsActive(null);
			postDTO.setCommentCount(postDTO.getComments().size());
			postDTO.setComments(null);
			postDTO.setTags(null);
			postDTOList.add(postDTO);
		});
		int count = (int) postPage.getTotalElements();
		return new PostPageDTO(count, postDTOList);
	}

	private PostDTO postToPostDTO(Post post) {
		return new PostDTO(
				post.getId(),
				Timestamp.valueOf(post.getPostTime()).getTime() / 1000,
				post.isActive() && post.getModerationStatus().equals(ModerationStatus.ACCEPTED),
				userToUserForPostDTO(post.getUser()),
				post.getTitle(),
				null,
				post.getText(),
				postRepository.likesCountOnPost(post),
				postRepository.dislikesCountOnPost(post),
				null,
				post.getViewCount(),
				listCommentToListCommentDTO(commentRepository.findAllByPostId(post.getId())),
				tagService.findTagsForPost(post.getId())
		);
	}

	private List<CommentDTO> listCommentToListCommentDTO(List<Comment> commentList) {
		List<CommentDTO> commentDTOList = new ArrayList<>();
		commentList.forEach(comment -> {
			User user =
					userRepository.findById(comment.getUserId())
							.orElseThrow(() -> new UsernameNotFoundException("Can't found author of comments with id="
									+ comment.getId()));
			CommentDTO commentDTO = new CommentDTO(
					comment.getId(),
					Timestamp.valueOf(comment.getTime()).getTime() / 1000,
					comment.getText(),
					new UserForCommentDTO(
							user.getId(),
							user.getName(),
							user.getPhotoPath()
					));
			commentDTOList.add(commentDTO);
		});
		return commentDTOList;
	}

}