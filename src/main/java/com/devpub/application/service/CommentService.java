package com.devpub.application.service;

import com.devpub.application.dto.request.CommentRequest;
import com.devpub.application.dto.response.BadRequestDTO;
import com.devpub.application.dto.response.CommentResponse;
import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.model.Comment;
import com.devpub.application.model.User;
import com.devpub.application.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
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


	public ResponseEntity<?> postComment(CommentRequest commentRequest, Principal principal) {
		User user = userService.getUser(principal);
		Integer parentId = commentRequest.getParentId();
		int postId = commentRequest.getPostId();

		//check 404 errors
		if (parentId != null) {
			if (commentRepository.findById(parentId).isEmpty()) {
				return ResponseEntity.badRequest().body(new BadRequestDTO("Comment not exist anymore"));
			}
		}
		if (postService.findById(postId).isEmpty()) {
			return ResponseEntity.badRequest().body(new BadRequestDTO("Post not exist anymore"));
		}

		//check comment errors
		Map<String, String> errors = checkErrors(commentRequest);

		if (errors.size() == 0) {
			Comment comment = new Comment();
			comment.setParentId(parentId);
			comment.setPostId(postId);
			comment.setUserId(user.getId());
			comment.setText(commentRequest.getText());
			comment.setTime(LocalDateTime.now());
			commentRepository.save(comment);
			return ResponseEntity.ok(new CommentResponse(comment.getId()));
		} else {
			return ResponseEntity.ok(new ResultDTO(false, errors));
		}
	}

	//Private methods=============================================================

	private Map<String, String> checkErrors(CommentRequest commentRequest) {
		Map<String, String> errors = new HashMap<>();
		if (commentRequest.getText().length() < commentMinLength) {
			errors.put("text", TEXT_TO_SHORT_ERROR);
		}
		return errors;
	}


}
