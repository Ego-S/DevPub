package com.devpub.application.repository;

import com.devpub.application.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	@Query("SELECT p FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1")
	Page<Post> findAllAcceptedPostsBefore(LocalDateTime timeBefore, Pageable pageable);

	@Query(value = "SELECT * FROM posts WHERE is_active=true AND moderation_status='ACCEPTED' AND time <= ?1 " +
			"ORDER BY (SELECT COUNT(*) FROM post_comments WHERE post_comments.post_id=posts.id) DESC",
			countQuery = "SELECT * FROM posts WHERE is_active=true AND moderation_status='ACCEPTED' AND time <= ?1",
			nativeQuery = true)
	Page<Post> findAllPopularAcceptedPostsBefore (LocalDateTime timeBefore, Pageable pageable);

	@Query(value = "SELECT * FROM posts WHERE is_active=true AND moderation_status='ACCEPTED' AND time <=?1 " +
			"ORDER BY (SELECT COUNT(*) FROM post_votes WHERE post_id=posts.id AND value=1) DESC",
			countQuery = "SELECT * FROM posts WHERE is_active=true AND moderation_status='ACCEPTED' AND time <= ?1",
			nativeQuery = true)
	Page<Post> findAllBestAcceptedPostsBefore (LocalDateTime timeBefore, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1 ORDER BY postTime DESC")
	Page<Post> findAllAcceptedPostsBeforeSortedByTimeDesc(LocalDateTime timeBefore, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1 ORDER BY postTime ASC")
	Page<Post> findAllAcceptedPostsBeforeSortedByTimeAsc(LocalDateTime timeBefore, Pageable pageable);

	@Query("SELECT COUNT(v) FROM Vote v WHERE postId=?1 AND value=1")
	int likesCountOnPost(Post post);

	@Query("SELECT COUNT(v) FROM Vote v WHERE postId=?1 AND value=-1")
	int dislikesCountOnPost(Post post);

	@Query("SELECT COUNT(c) FROM Comment c WHERE postId=?1")
	int commentCountByPost(int postId);
}
