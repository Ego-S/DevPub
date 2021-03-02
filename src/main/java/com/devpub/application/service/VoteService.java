package com.devpub.application.service;

import com.devpub.application.model.Post;
import com.devpub.application.model.User;
import com.devpub.application.model.Vote;
import com.devpub.application.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VoteService {
	private final VoteRepository voteRepository;

	@Autowired
	public VoteService(
			VoteRepository voteRepository
	) {
		this.voteRepository = voteRepository;
	}

	public Optional<Vote> findByUserIdAndPostId(User user, Post post) {
		return voteRepository.findByUserIdAndPostId(user, post);
	}

	public void save(Vote vote) {
		voteRepository.save(vote);
	}

	public void deleteById(int id) {
		voteRepository.deleteById(id);
	}
}
