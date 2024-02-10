package com.Jason.DevInternHubBackend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// Note `@RepositoryRestResource` is not required for a repository to be exported
// It is only used to change the export details
@RepositoryRestResource
public interface JobRepository extends CrudRepository<Job, Long> {
	List<Job> findByTitleContainingIgnoreCase(@Param("title") String partOfName);
}
