package com.devpub.application.repository;

import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	//TODO подумать, можно ли избавиться от натива?
	@Query(value = "SELECT * FROM posts WHERE is_active=true AND moderation_status='ACCEPTED' AND time <=?1 " +
			"ORDER BY (SELECT COUNT(*) FROM post_votes WHERE post_id=posts.id AND value=1) DESC",
			countQuery = "SELECT COUNT(*) FROM posts WHERE is_active=true AND moderation_status='ACCEPTED' AND time <= ?1",
			nativeQuery = true)
	Page<Post> findAllBestAcceptedPostsBefore (LocalDateTime timeBefore, Pageable pageable);

	@Query(value = "SELECT p FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1")
	Page<Post> findAll(LocalDateTime now ,Pageable pageable);

	@Query("SELECT COUNT(v) FROM Vote v WHERE postId=?1 AND value=1")
	int likesCountOnPost(Post post);

	@Query("SELECT COUNT(v) FROM Vote v WHERE postId=?1 AND value=-1")
	int dislikesCountOnPost(Post post);

	@Query("SELECT COUNT(c) FROM Comment c WHERE postId=?1")
	int commentCountByPost(int postId);

	@Query(value = "SELECT p FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1 AND (title LIKE ?2 OR text LIKE ?2)",
			countQuery = "SELECT COUNT(p) FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1 AND (title LIKE ?2 OR text LIKE ?2)")
	Page<Post> search(LocalDateTime timeBefore ,String query, Pageable pageable);

	@Query(value = "SELECT p FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1 AND postTime BETWEEN ?2 AND ?3",
		  countQuery = "SELECT COUNT(p) FROM Post p WHERE isActive=true AND moderationStatus='ACCEPTED' AND postTime <= ?1 AND postTime BETWEEN ?2 AND ?3")
	Page<Post> findAllByDate(LocalDateTime now, LocalDateTime from, LocalDateTime to, Pageable pageable);

	@Query(value = "SELECT p FROM Post p JOIN TagToPost ttp ON p.id = ttp.postId WHERE ttp.tagId = ?2 AND p.isActive=true AND p.moderationStatus='ACCEPTED' AND p.postTime <= ?1",
			countQuery = "SELECT COUNT(p) FROM Post p JOIN TagToPost ttp ON p.id = ttp.postId WHERE ttp.tagId = ?2 AND p.isActive=true AND p.moderationStatus='ACCEPTED' AND p.postTime <= ?1")
	Page<Post> findAllByTag(LocalDateTime now, int tagId, Pageable pageable);


	@Query("SELECT COUNT(p) FROM Post p WHERE moderationStatus = ?1")
	int countByStatus (ModerationStatus status);
}
