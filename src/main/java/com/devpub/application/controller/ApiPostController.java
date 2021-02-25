package com.devpub.application.controller;

import com.devpub.application.dto.response.PostDTO;
import com.devpub.application.dto.response.PostPageDTO;
import com.devpub.application.service.PostService;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;

@Data
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

	private final PostService postService;

	@GetMapping
	public ResponseEntity<PostPageDTO> findAllPost(
			@RequestParam(name = "offset") int offset,
			@RequestParam(name = "limit") int limit,
			@RequestParam(name = "mode") String mode
	) {
		return ResponseEntity.ok(postService.getPostPage(offset, limit, mode));
	}

	@GetMapping("search")
	public ResponseEntity<PostPageDTO> search(
			@RequestParam(name = "offset") int offset,
			@RequestParam(name = "limit") int limit,
			@RequestParam(name = "query") String query
	) {
		return ResponseEntity.ok(postService.getPostsPageLike(offset, limit, query));
	}

	@GetMapping("byDate")
	public ResponseEntity<PostPageDTO> findByDate(
			@RequestParam(name = "offset") int offset,
			@RequestParam(name = "limit") int limit,
			@RequestParam(name = "date")
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		return ResponseEntity.ok(postService.getPostsPageByDate(offset, limit, date));
	}

	@GetMapping("byTag")
	public ResponseEntity<PostPageDTO> findByTag(
			@RequestParam(name = "offset") int offset,
			@RequestParam(name = "limit") int limit,
			@RequestParam(name = "tag") String tag
	) {
		return ResponseEntity.ok(postService.getPostsPageByTag(offset, limit, tag));
	}

	@GetMapping("/my")
	@PreAuthorize("hasAuthority('user')")
	public ResponseEntity<PostPageDTO> myPosts(
			@RequestParam(name = "offset") int offset,
			@RequestParam(name = "limit") int limit,
			@RequestParam(name = "status") String status,
			Principal principal
	) {
		return ResponseEntity.ok(postService.myPosts(offset, limit, status, principal));
	}

	@GetMapping("/moderation")
	@PreAuthorize("hasAuthority('moderator')")
	public ResponseEntity<PostPageDTO> moderation(
			@RequestParam(name = "offset") int offset,
			@RequestParam(name = "limit") int limit,
			@RequestParam(name = "status") String status,
			Principal principal
	) {
		return ResponseEntity.ok(postService.postsForModeration(offset, limit, status, principal));
	}

	@GetMapping("/{ID}")
	public ResponseEntity<PostDTO> getPost(
			@PathVariable("ID") int id,
			Principal principal
	) {
		return postService.getPostById(id, principal);
	}
}
