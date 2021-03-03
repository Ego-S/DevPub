package com.devpub.application.repository;

import com.devpub.application.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

	@Query("SELECT id FROM Tag t WHERE name=:name")
	int getIdByName(String name);

	@Query("SELECT t.name FROM Tag t JOIN TagToPost ttp ON t.id=ttp.tagId WHERE ttp.postId=:postId")
	List<String> findTagsForPost(int postId);

	Tag getTagByName(String tagName);

	List<Tag> findByNameLike(String query);

	//Have to complete this query here cause "The dependencies of some of the beans in the application context form a
	// cycle" when i try inject PostService into TagService
	@Query("SELECT COUNT(p) FROM Post p")
	int getAllPostCount();
}
