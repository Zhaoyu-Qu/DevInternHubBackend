package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
public class TechnologyTest extends BaseTest {
	private Technology java;

	@BeforeEach
	protected void setUp() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();

		java = new Technology("Java");
	}

	@Test
	public void testEquals() {
		Technology t1, t2;
		t1 = new Technology();
		t2 = new Technology();
		assertTrue(t1.equals(t2));
		t1.setName("foo");
		assertFalse(t1.equals(t2));
		t2.setName("foo");
		assertTrue(t1.equals(t2));
	}

	@Test
	public void testSetJobs() {
		// test idempotency
		java.getJobs().add(new Job("foo"));
		java.getJobs().add(new Job("foo"));
		assertTrue(java.getJobs().size() == 1);
	}
}
