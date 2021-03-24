package com.devpub.application.service;

import com.devpub.application.dto.exception.BadRequestException;
import com.devpub.application.dto.request.PostModerationRequest;
import com.devpub.application.dto.request.PostRequest;
import com.devpub.application.dto.request.VoteRequest;
import com.devpub.application.dto.response.*;
import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.model.Comment;
import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.model.Vote;
import com.devpub.application.repository.CommentRepository;
import com.devpub.application.repository.PostRepository;
import lombok.Data;
import org.jsoup.Jsoup;
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
import java.time.*;
import java.util.*;

@Data
@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserService userService;
	private final CommentRepository commentRepository;

	private final VoteService voteService;
	private final SettingsService settingsService;
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
			UserService userService,
			CommentRepository commentRepository,
			VoteService voteService,
			SettingsService settingsService,
			TagService tagService) {
		this.postRepository = postRepository;
		this.commentRepository = commentRepository;
		this.tagService = tagService;
		this.voteService = voteService;
		this.settingsService = settingsService;
		this.userService = userService;
	}

	//Basic methods for request to repository======================================

	public Optional<Post> findById(int id) {
		return postRepository.findById(id);
	}

	//Methods for Controllers request==============================================
	public PostPageDTO getPostPage(int offset, int limit, String mode) {
		Page<Post> postPage;
		Sort sort;
		Pageable pageable;
		int page = offset / limit;

		switch(mode) {
			case "best" :
				pageable = PageRequest.of(page, limit);
				postPage = postRepository.findAllBestAcceptedPostsBefore(true, ModerationStatus.ACCEPTED.toString(),
						LocalDateTime.now() ,pageable);
				return mappingPostPageToPostPageDTO(postPage);
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
		postPage = postRepository
				.findAll(true, ModerationStatus.ACCEPTED, LocalDateTime.now() ,pageable);

		return mappingPostPageToPostPageDTO(postPage);
	}

	public PostPageDTO getPostsPageLike(int offset, int limit, String query) {
		int pageNumber = offset / limit;
		Page<Post> page;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		if (query.length() == 0) {
			page = postRepository
					.findAll(true, ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneId.of("UTC")), pageable);
		} else {
			query = "%" + query + "%";
			page = postRepository
					.search(true, ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneId.of("UTC")),
							query, pageable);
		}

		return mappingPostPageToPostPageDTO(page);
	}

	public PostPageDTO getPostsPageByDate(int offset, int limit, LocalDate date) {
		int pageNumber = offset / limit;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		LocalDateTime from = date.atTime(LocalTime.MIN);
		LocalDateTime to = date.atTime(LocalTime.MAX);

		Page<Post> page = postRepository
				.findAllByDate(true, ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneId.of("UTC")),
						from, to, pageable);

		return mappingPostPageToPostPageDTO(page);
	}

	public PostPageDTO getPostsPageByTag(int offset, int limit, String tag) {
		int pageNumber = offset / limit;
		Pageable pageable = PageRequest.of(pageNumber, limit);

		int tagId = tagService.getIdByName(tag);
		Page<Post> page = postRepository
				.findAllByTag(true, ModerationStatus.ACCEPTED, LocalDateTime.now(ZoneId.of("UTC")),
						tagId, pageable);

		return mappingPostPageToPostPageDTO(page);
	}

	public PostPageDTO myPosts(int offset, int limit, String status, Principal principal) {
		User user = userService.getUser(principal);
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

		return mappingPostPageToPostPageDTO(postPage);
	}

	public PostPageDTO getPostsForModeration(int offset, int limit, String status, Principal principal) {
		User moderator = userService.getUser(principal);
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
		return mappingPostPageToPostPageDTO(postPage);
	}

	public PostDTO getPostById(int id, Principal principal) {
		// Does it exist?
		Post post = postRepository.findPostById(id).orElseThrow(BadRequestException::new);

		// Who is try to see it? Should we increment viewCount?
		User user;
		if (principal != null) {
			user = userService.getUser(principal);
		} else {
			user = null;
		}
		// Increment viewCount if we should
		if (user == null || user != post.getUser() || !user.isModerator()) {
			post.setViewCount(post.getViewCount() + 1);
			postRepository.save(post);
		}
		return mappingPostToPostDTO(post);
	}

	public ResultDTO postPost(PostRequest postRequest, Principal principal) {
		User user = userService.getUser(principal);
		Map<String, String> errors = getErrorsByPostPublication(postRequest);

		if (errors.size() != 0) {
			return new ResultDTO(false, errors);
		} else {
			Post post = new Post();
			ModerationStatus status = settingsService.getStatusForNewPost();
			return postPostRequest(postRequest, user, post, status);
		}
	}

	public ResultDTO putPost(int id, PostRequest postRequest, Principal principal) {
		User user = userService.getUser(principal);
		Map<String, String> errors = getErrorsByPostPublication(postRequest);

		if (errors.size() != 0) {
			return new ResultDTO(false, errors);
		} else {
			Post post = postRepository.getOne(id);
			ModerationStatus postStatus = post.getModerationStatus();
			if (!user.isModerator()) {
				postStatus = settingsService.getStatusForNewPost();
			}
			return postPostRequest(postRequest, user, post, postStatus);
		}
	}

	public ResultDTO vote(int value, VoteRequest voteRequest, Principal principal) {
		boolean result = true;
		User user = userService.getUser(principal);
		Post post = postRepository.findById(voteRequest.getPostId()).orElseThrow(BadRequestException::new);

		Optional<Vote> optionalVote = voteService.findByUserIdAndPostId(user, post);

		if (optionalVote.isPresent()) {
			Vote vote = optionalVote.get();
			if (value == vote.getValue()) {
				//if it's the same vote - do nothing
				result = false;
			} else {
				//if values is different - modify the vote
				vote.setValue((byte) value);
				voteService.save(vote);
			}
		//if it's a new vote - save it
		} else {
			Vote vote = new Vote();
			vote.setPostId(post);
			vote.setTime(LocalDateTime.now(ZoneId.of("UTC")));
			vote.setUserId(user);
			vote.setValue((byte) value);
			voteService.save(vote);
		}
		return new ResultDTO(result, null);
	}


	public ResultDTO postModeration(PostModerationRequest postModerationRequest, Principal principal) {
		boolean result = true;
		User moderator = userService.getUser(principal);
		if (moderator.isModerator()) {
			Post post = postRepository.getOne(postModerationRequest.getPostId());
			if (post == null) {
				result = false;
			} else {
				switch (postModerationRequest.getDecision()) {
					case "accept" :
						post.setModerationStatus(ModerationStatus.ACCEPTED);
						post.setModerator(moderator);
						break;
					case "decline" :
						post.setModerationStatus(ModerationStatus.DECLINED);
						post.setModerator(moderator);
						break;
				}
				postRepository.save(post);
			}
		} else {
			result = false;
		}

		return new ResultDTO(result, null);
	}


	public CalendarDTO getCalendar(Integer year) {
		//if request param "year" is empty, get calendar for current year
		if (year == null) {
			year = LocalDate.now(ZoneId.of("UTC")).getYear();
		}

		//init set and map for response entity
		TreeSet<Integer> years = new TreeSet<>();
		TreeMap<LocalDate, Integer> dateToPostCount = new TreeMap<>();

		//fill set and map
		for (Post post : postRepository
				.findAllByStatusAndIsActiveBefore(ModerationStatus.ACCEPTED, true,
						LocalDateTime.now())) {
			LocalDateTime postTime = post.getPostTime();
			//add year to yearsSet
			years.add(postTime.getYear());

			if (postTime.getYear() == year) {
				LocalDate postDate = postTime.toLocalDate();
				dateToPostCount.merge(postDate, 1, Integer::sum);
			}
		}

		return new CalendarDTO(years, dateToPostCount);
	}

	//Private methods==================================================================

	private Map<String, String> getErrorsByPostPublication(PostRequest postRequest) {
		Map<String, String> errors = new HashMap<>();

		if (postRequest.getText().length() < textMinLength) {
			errors.put("text", TEXT_TOO_SHORT_ERROR);
		}
		if (postRequest.getTitle().length() < titleMinLength) {
			errors.put("title", TITLE_TOO_SHORT_ERROR);
		}

		return errors;
	}

	private ResultDTO postPostRequest(PostRequest postRequest, User user, Post post, ModerationStatus moderationStatus) {
		post.setActive(postRequest.getActive() == 1);
		post.setModerationStatus(moderationStatus);
		post.setUser(user);
		post.setPostTime(longToLocalDateTime(Math.max(System.currentTimeMillis() / 1000, postRequest.getTimestamp())));
		post.setTitle(postRequest.getTitle());
		post.setText(postRequest.getText());

		postRepository.save(post);

		for (String tagName : postRequest.getTags()) {
			tagService.saveTag(tagName, post.getId());
		}

		return new ResultDTO(true, null);
	}

	private LocalDateTime longToLocalDateTime(long sec) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(sec), ZoneId.systemDefault());
	}

	private UserForPostDTO userToUserForPostDTO(User user) {
		return new UserForPostDTO(user.getId(), user.getName());
	}

	private String getAnnounceFromText(String text) {
		String announce = Jsoup.parse(text).text();
		return (announce.length() > announceLength)
				? announce.substring(0, announceLength - 1) + "..."
				: announce;
	}

	private PostPageDTO mappingPostPageToPostPageDTO(Page<Post> postPage) {
		List<PostDTO> postDTOList = new ArrayList<>();
		postPage.getContent().forEach(post -> {
			PostDTO postDTO = mappingPostToPostDTO(post);
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

	private PostDTO mappingPostToPostDTO(Post post) {
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
				mappingListCommentToListCommentDTO(commentRepository.findAllByPostId(post.getId())),
				tagService.findTagsForPost(post.getId())
		);
	}

	private List<CommentDTO> mappingListCommentToListCommentDTO(List<Comment> commentList) {
		List<CommentDTO> commentDTOList = new ArrayList<>();
		commentList.forEach(comment -> {
			User user =
					userService.findById(comment.getUserId())
							.orElseThrow(() -> new UsernameNotFoundException("Can't found author of comments with id="
									+ comment.getId()));
			CommentDTO commentDTO = new CommentDTO(
					comment.getId(),
					comment.getTime().toEpochSecond(ZoneOffset.UTC),
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