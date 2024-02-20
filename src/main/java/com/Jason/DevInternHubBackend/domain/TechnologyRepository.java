package com.Jason.DevInternHubBackend.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface TechnologyRepository extends CrudRepository<Technology, Long> {
	Optional<Technology> findByNameIgnoreCase(String name);
	boolean existsByNameIgnoreCase(String name);
}
