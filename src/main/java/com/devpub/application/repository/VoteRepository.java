package com.devpub.application.repository;

import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.model.Vote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends CrudRepository<Vote, Integer> {

	Optional<Vote> findByUserIdAndPostId(User user, Post post);

	@Modifying
	@Query("DELETE FROM Vote v WHERE v.id=:id")
	void deleteById(Integer id);
}
