package com.Jason.DevInternHubBackend.domain;


import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;

@SpringBootTest
public class JobRepositoryTest extends BaseRepositoryTest<Job, Long> {
	@Override
	public CrudRepository<Job, Long> getRepository() {
		return jobRepository;
	}

	@Override
	public Job createEntity() {
		return new Job();
	}
	
	@Test
	public void testSaveDuplicate() {
		Job j1 = new Job();
		j1.setUrl("foo");
		Job j2 = new Job();
		j2.setUrl("foo");
		jobRepository.save(j1);
		assertThrows(DataIntegrityViolationException.class, () -> {
			jobRepository.save(j2);
		});
	}
	
	@Test
	public void testFindByUrlIgnoreCase() {
		Job job = new Job("foo");
		job.setUrl("bar");
		jobRepository.save(job);
		assertTrue(jobRepository.findByUrlIgnoreCase("Bar").isPresent());

	}
}
