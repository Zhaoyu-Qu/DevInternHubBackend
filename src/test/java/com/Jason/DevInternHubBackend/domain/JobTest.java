package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobTest extends BaseTest {
	private Job backendJob;

	@BeforeEach
	protected void setUp() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();
		backendJob = new Job("Backend Job");
		backendJob.setUrl("www.foo.com");
	}

	@Test
	public void testSetBookmark() {
		// test idempotency
		backendJob.getBookmarkHolders().add(new AppUser("foo", "bar", "admin"));
		backendJob.getBookmarkHolders().add(new AppUser("foo", "bar", "admin"));
		assertTrue(new AppUser("foo", "bar", "admin").equals(new AppUser("foo", "bar", "admin")));
		assertTrue(backendJob.getBookmarkHolders().size() == 1);
	}

	@Test
	public void testSetTechnologies() {
		// test idempotency
		backendJob.getTechnologies().add(new Technology("foo"));
		backendJob.getTechnologies().add(new Technology("foo"));
		assertTrue(backendJob.getTechnologies().size() == 1);
	}
}
