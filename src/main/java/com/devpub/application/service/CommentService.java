package com.devpub.application.service;

import com.devpub.application.dto.exception.BadRequestException;
import com.devpub.application.dto.request.CommentRequest;
import com.devpub.application.dto.response.CommentResponse;
import com.devpub.application.model.Comment;
import com.devpub.application.model.User;
import com.devpub.application.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Service
public class CommentService {
	private final String TEXT_TO_SHORT_ERROR = "Comment is empty or too short";
	private final UserService userService;
	private final PostService postService;
	private final CommentRepository commentRepository;

	@Value("${commentMinLength}")
	private int commentMinLength;

	@Autowired
	public CommentService(
			UserService userService,
			PostService postService,
			CommentRepository commentRepository
	) {
		this.userService = userService;
		this.postService = postService;
		this.commentRepository = commentRepository;
	}


	public CommentResponse postComment(CommentRequest commentRequest, Principal principal) {
		User user = userService.getUser(principal);
		Integer parentId = commentRequest.getParentId();
		int postId = commentRequest.getPostId();

		//check 404 errors
		if (parentId != null) {
			commentRepository.findById(parentId).orElseThrow(BadRequestException::new);
		}
		postService.findById(postId).orElseThrow(BadRequestException::new);

		//check comment errors
		Map<String, String> errors = checkErrors(commentRequest);

		if (errors.size() == 0) {
			Comment comment = mappingCommentRequestToComment(
							commentRequest, user, new Comment(), LocalDateTime.now(ZoneId.systemDefault()));
			commentRepository.save(comment);
			return new CommentResponse(comment.getId(), null, null);
		} else {
			return new CommentResponse(null, false, errors);
		}
	}

	//Private methods=============================================================

	private Comment mappingCommentRequestToComment(CommentRequest commentRequest,
												   User user,
												   Comment comment,
												   LocalDateTime time) {
		comment.setParentId(commentRequest.getParentId());
		comment.setPostId(commentRequest.getPostId());
		comment.setUserId(user.getId());
		comment.setText(commentRequest.getText());
		comment.setTime(time);
		return comment;
	}

	private Map<String, String> checkErrors(CommentRequest commentRequest) {
		Map<String, String> errors = new HashMap<>();
		if (commentRequest.getText().length() < commentMinLength) {
			errors.put("text", TEXT_TO_SHORT_ERROR);
		}
		return errors;
	}


}
