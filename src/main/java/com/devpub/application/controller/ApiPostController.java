package com.devpub.application.controller;

import com.devpub.application.dto.PostPageDTO;
import com.devpub.application.service.PostService;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Data
@RestController
@RequestMapping("/api/post")
public class ApiPostController {

	private final PostService postService;

	@GetMapping
	public ResponseEntity<PostPageDTO> findAllPost(@RequestParam(name = "offset") int offset,
												  @RequestParam(name = "limit") int limit,
												  @RequestParam(name = "mode") String mode) {
		return new ResponseEntity<>(postService.getPostsPage(offset, limit, mode), HttpStatus.OK);
	}

	@GetMapping("search")
	public ResponseEntity<PostPageDTO> search(@RequestParam(name = "offset") int offset,
											  @RequestParam(name = "limit") int limit,
											  @RequestParam(name = "query") String query) {
		return new ResponseEntity<>(postService.getPostsPageLike(offset, limit, query), HttpStatus.OK);
	}

	@GetMapping("byDate")
	public ResponseEntity<PostPageDTO> findByDate(@RequestParam(name = "offset") int offset,
												 @RequestParam(name = "limit") int limit,
												 @RequestParam(name = "date")
													  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return new ResponseEntity<>(postService.getPostsPageByDate(offset, limit, date), HttpStatus.OK);
	}

	@GetMapping("byTag")
	public ResponseEntity<PostPageDTO> findByTag(@RequestParam(name = "offset") int offset,
												 @RequestParam(name = "limit") int limit,
												 @RequestParam(name = "tag") String tag) {
		return new ResponseEntity<>(postService.getPostsPageByTag(offset, limit, tag), HttpStatus.OK);
	}
}
