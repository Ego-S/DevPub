package com.devpub.application.controller;

import com.devpub.application.dto.request.CommentRequest;
import com.devpub.application.dto.request.PostModerationRequest;
import com.devpub.application.dto.request.ProfileRedactionRequest;
import com.devpub.application.dto.response.*;
import com.devpub.application.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {

	private final InitResponse initResponse;
	private final SettingsService settingsService;
	private final TagService tagService;
	private final PostService postService;
	private final StatisticService statisticService;
	private final UploadService uploadService;
	private final UserService userService;
	private final CommentService commentService;

	@Autowired
	public ApiGeneralController(
			InitResponse initResponse,
			SettingsService settingsService,
			TagService tagService,
			PostService postService,
			StatisticService statisticService,
			UploadService uploadService,
			UserService userService,
			CommentService commentService
			) {
		this.initResponse = initResponse;
		this.settingsService = settingsService;
		this.tagService = tagService;
		this.postService = postService;
		this.statisticService = statisticService;
		this.uploadService = uploadService;
		this.userService = userService;
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
		return ResponseEntity.ok(settingsService.putSettings(settingsDTO));
	}

	@PostMapping("/comment")
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<CommentResponse> postComment(
			@RequestBody CommentRequest commentRequest,
			Principal principal
			) {
		return ResponseEntity.ok(commentService.postComment(commentRequest, principal));
	}

	@GetMapping("/tag")
	public ResponseEntity<TagsDTO> getTags(
			@RequestParam(name = "query", required = false) String query
	) {
		return ResponseEntity.ok(tagService.getTags(query));
	}

	@PostMapping("/moderation")
	@PreAuthorize("hasAuthority('moderator')")
	public ResponseEntity<ResultDTO> postModeration(
			@RequestBody PostModerationRequest moderation,
			Principal principal
	) {
		return ResponseEntity.ok(postService.postModeration(moderation, principal));
	}

	@GetMapping("/statistics/all")
	public ResponseEntity<StatisticDTO> getBlogStatistic(
			Principal principal
	) {
		return ResponseEntity.ok(statisticService.getBlogStatistic(principal));
	}

	@GetMapping("/statistics/my")
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<StatisticDTO> getMyStatistic(
			Principal principal
	) {
		return ResponseEntity.ok(statisticService.getMyStatistic(principal));
	}

	@GetMapping("/calendar")
	public ResponseEntity<CalendarDTO> calendar(@RequestParam(name = "year", required = false) Integer year) {
		return ResponseEntity.ok(postService.getCalendar(year));
	}

	@PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<?> postImage(@RequestParam("image") MultipartFile file) {
		return uploadService.uploadImage(file);
	}

	@PostMapping(value = "/profile/my",
			consumes = {MediaType.APPLICATION_JSON_VALUE})
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<ResultDTO> profileRedactionWithoutPhoto(
			@RequestBody ProfileRedactionRequest body,
			Principal principal
	) {
		return ResponseEntity.ok(userService.profileRedaction(
				null, body.getName(), body.getEmail(), body.getPassword(), body.getRemovePhoto(), principal));
	}

	@PostMapping(value = "/profile/my",
			consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<?> profileRedactionWithPhoto(
			@RequestPart(name = "photo", required = false) MultipartFile photo,
			@RequestPart(name = "removePhoto", required = false) String removePhoto,
			@RequestPart(name = "name", required = false) String name,
			@RequestPart(name = "email", required = false) String email,
			@RequestPart(name = "password", required = false) String password,
			Principal principal
	) {
		Boolean remove = removePhoto != null ? removePhoto.equals("1") : null;
		return ResponseEntity.ok(userService.profileRedaction(
				photo, name, email, password, remove, principal
		));
	}
}
