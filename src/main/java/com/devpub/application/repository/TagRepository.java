package com.devpub.application.repository;

import com.devpub.application.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

	@Query("SELECT id FROM Tag t WHERE name = ?1")
	int getIdByName(String name);
}
