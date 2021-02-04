package com.devpub.application.controller;

import com.devpub.application.dto.PostPageDTO;
import com.devpub.application.service.PostService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
