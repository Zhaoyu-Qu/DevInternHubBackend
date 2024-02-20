package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
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
	
	@Test
	public void testExistsByNameIgnoreCase() {
		technologyRepository.save(new Technology("foo"));
		assertTrue(technologyRepository.existsByNameIgnoreCase("foo"));
	}
}
