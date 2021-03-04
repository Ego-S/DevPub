package com.devpub.application.controller;

import com.devpub.application.dto.request.CommentRequest;
import com.devpub.application.dto.request.PostModerationRequest;
import com.devpub.application.dto.response.InitResponse;
import com.devpub.application.dto.response.ResultDTO;
import com.devpub.application.dto.response.SettingsDTO;
import com.devpub.application.dto.response.TagsDTO;
import com.devpub.application.service.CommentService;
import com.devpub.application.service.PostService;
import com.devpub.application.service.SettingsService;
import com.devpub.application.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

	private final InitResponse initResponse;
	private final SettingsService settingsService;
	private final TagService tagService;
	private final PostService postService;
	private final CommentService commentService;

	@Autowired
	public ApiGeneralController(
			InitResponse initResponse,
			SettingsService settingsService,
			TagService tagService,
			PostService postService,
			CommentService commentService
			) {
		this.initResponse = initResponse;
		this.settingsService = settingsService;
		this.tagService = tagService;
		this.postService = postService;
		this.commentService = commentService;
	}

	@GetMapping("/init")
	public ResponseEntity<InitResponse> init() {
		return new ResponseEntity<>(initResponse, HttpStatus.OK);
	}

	@GetMapping("/settings")
	public ResponseEntity<SettingsDTO> getGlobalSettings() {
		return new ResponseEntity<>(settingsService.getSettings(), HttpStatus.OK);
	}

	@PutMapping("/settings")
	@PreAuthorize("hasAuthority('moderator')")
	public ResponseEntity<ResultDTO> putGlobalSettings(
			@RequestBody SettingsDTO settingsDTO
	) {
		return settingsService.putSettings(settingsDTO);
	}

	@PostMapping("/comment")
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<?> postComment(
			@RequestBody CommentRequest commentRequest,
			Principal principal
			) {
		return commentService.postComment(commentRequest, principal);
	}

	@GetMapping("/tag")
	public ResponseEntity<TagsDTO> getTags(
			@RequestParam(name = "query", required = false) String query
	) {
		return tagService.getTags(query);
	}

	@PostMapping("/moderation")
	@PreAuthorize("hasAuthority('moderator')")
	public ResponseEntity<ResultDTO> postModeration(
			@RequestBody PostModerationRequest moderation,
			Principal principal
	) {
		return postService.postModeration(moderation, principal);
	}
}
