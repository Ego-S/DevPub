package com.devpub.application.repository;

import com.devpub.application.model.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Integer> {
	List<Comment> findAllByPostId(int postId);
}
