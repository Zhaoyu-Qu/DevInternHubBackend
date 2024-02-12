package com.Jason.DevInternHubBackend.domain;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.CrudRepository;

@SpringBootTest
public class TechnologyRepositoryTest extends BaseRepositoryTest<Technology, Long> {
	@Override
	public CrudRepository<Technology, Long> getRepository() {
		return technologyRepository;
	}

	@Override
	public Technology createEntity() {
		return new Technology();
	}
}
