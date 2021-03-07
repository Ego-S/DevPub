package com.devpub.application.repository;

import com.devpub.application.model.TagToPost;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagToPostRepository extends CrudRepository<TagToPost, Integer> {
	TagToPost findByPostIdAndTagId(int postId, int id);

	@Query("SELECT COUNT(ttp) FROM TagToPost ttp WHERE ttp.tagId=:tagId")
	int countByTagId(Integer tagId);
}
