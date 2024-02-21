package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
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

	@Test
	public void testSaveDuplicate() {
		technologyRepository.save(new Technology("foo"));
		assertThrows(DataIntegrityViolationException.class, () -> {
			technologyRepository.save(new Technology("foo"));
		});
	}
}
