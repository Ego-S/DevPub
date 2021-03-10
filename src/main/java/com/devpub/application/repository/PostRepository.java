package com.devpub.application.repository;

import com.devpub.application.enums.ModerationStatus;
import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

	//TODO подумать, можно ли избавиться от натива?
	@Query(value = "SELECT * FROM posts WHERE is_active=:isActive AND moderation_status=:status AND time<=:timeBefore " +
			"ORDER BY (SELECT COUNT(*) FROM post_votes WHERE post_id=posts.id AND value=1) DESC",
			countQuery = "SELECT COUNT(*) FROM posts WHERE is_active=:isActive AND moderation_status=:status " +
					"AND time<=:timeBefore",
			nativeQuery = true)
	Page<Post> findAllBestAcceptedPostsBefore(
			boolean isActive,
			String status,
			LocalDateTime timeBefore,
			Pageable pageable
	);

	@Query(value = "SELECT p FROM Post p WHERE isActive=:isActive AND moderationStatus=:moderationStatus " +
			"AND postTime<=:now")
	Page<Post> findAll(
			boolean isActive,
			ModerationStatus moderationStatus,
			LocalDateTime now,
			Pageable pageable
	);

	@Query("SELECT COUNT(v) FROM Vote v WHERE postId=:post AND value=1")
	int likesCountOnPost(Post post);

	@Query("SELECT COUNT(v) FROM Vote v WHERE postId=:post AND value=-1")
	int dislikesCountOnPost(Post post);

	@Query("SELECT COUNT(c) FROM Comment c WHERE postId=:postId")
	int commentCountByPost(int postId);

	@Query(value = "SELECT p FROM Post p WHERE isActive=:isActive AND moderationStatus=:moderationStatus " +
			"AND postTime<=:timeBefore AND (title LIKE :query OR text LIKE :query)",
			countQuery = "SELECT COUNT(p) FROM Post p WHERE isActive=:isActive AND moderationStatus=:moderationStatus " +
					"AND postTime<=:timeBefore AND (title LIKE :query OR text LIKE :query)")
	Page<Post> search(
			boolean isActive,
			ModerationStatus moderationStatus,
			LocalDateTime timeBefore,
			String query,
			Pageable pageable
	);

	@Query(value = "SELECT p FROM Post p WHERE isActive=:isActive AND moderationStatus=:moderationStatus " +
			"AND postTime<=:now AND postTime BETWEEN :from AND :to",
			countQuery = "SELECT COUNT(p) FROM Post p WHERE isActive=:isActive AND moderationStatus=:moderationStatus " +
					"AND postTime<=:now AND postTime BETWEEN :from AND :to")
	Page<Post> findAllByDate(
			boolean isActive,
			ModerationStatus moderationStatus,
			LocalDateTime now,
			LocalDateTime from,
			LocalDateTime to,
			Pageable pageable
	);

	@Query(value = "SELECT p FROM Post p JOIN TagToPost ttp ON p.id=ttp.postId WHERE ttp.tagId=:tagId " +
			"AND p.isActive=:isActive AND p.moderationStatus=:moderationStatus AND p.postTime<=:now",
			countQuery = "SELECT COUNT(p) FROM Post p JOIN TagToPost ttp ON p.id=ttp.postId WHERE ttp.tagId=:tagId " +
					"AND p.isActive=:isActive AND p.moderationStatus=:moderationStatus AND p.postTime<=:now")
	Page<Post> findAllByTag(
			boolean isActive,
			ModerationStatus moderationStatus,
			LocalDateTime now,
			int tagId,
			Pageable pageable
	);

	@Query("SELECT COUNT(p) FROM Post p WHERE moderationStatus=:status")
	int countByStatus(ModerationStatus status);

	@Query(value = "SELECT p FROM Post p WHERE user=:user AND isActive=:isActive",
			countQuery = "SELECT COUNT(p) FROM Post p WHERE user=:user AND isActive=:isActive")
	Page<Post> findMyPostWithAnyStatus(
			User user,
			boolean isActive,
			Pageable pageable
	);

	@Query(value = "SELECT p FROM Post p WHERE user=:user AND isActive=:isActive AND moderationStatus=:status",
			countQuery = "SELECT COUNT(p) FROM Post p WHERE user=:user AND isActive=:isActive AND " +
					"moderationStatus=:status")
	Page<Post> findMyPosts(
			User user,
			boolean isActive,
			ModerationStatus status,
			Pageable pageable
	);

	Page<Post> findAllByModerationStatus(ModerationStatus status, Pageable pageable);

	@Query(value = "SELECT p FROM Post p WHERE moderationStatus=:status AND moderator=:moderator",
			countQuery = "SELECT COUNT(p) FROM Post p Where moderationStatus=:status AND moderator=:moderator")
	Page<Post> findAllModeratedByMe(ModerationStatus status, User moderator, Pageable pageable);

	Optional<Post> findPostById(int id);

	List<Post> findAllByUser(User user);

	@Query("FROM Post p WHERE p.moderationStatus=:status AND p.isActive=:isActive AND p.postTime<=:time")
	List<Post> findAllByStatusAndIsActiveBefore(ModerationStatus status, boolean isActive,LocalDateTime time);
}
