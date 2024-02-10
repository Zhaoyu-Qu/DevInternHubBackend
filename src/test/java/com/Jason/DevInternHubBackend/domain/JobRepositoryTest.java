package com.Jason.DevInternHubBackend.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.CrudRepository;

@DataJpaTest
public class JobRepositoryTest extends BaseRepositoryTest<Job, Long> {
	@Override
	public CrudRepository<Job, Long> getRepository() {
		return jobRepository;
	}

	@Override
	public Job createEntity() {
		return new Job();
	}
}
