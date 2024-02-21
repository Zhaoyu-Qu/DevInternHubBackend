package com.Jason.DevInternHubBackend.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, Long> {
	List<Job> findByTitleContainingIgnoreCase(String partOfName);
	Optional<Job> findByUrlIgnoreCase(String url);
}