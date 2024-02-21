package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CompanyTest extends BaseTest {
	private Company microsoft;

	@BeforeEach
	protected void setUp() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();

		microsoft = new Company("Microsoft", "www.microsoft.com");
	}

	@Test
	public void testEquals() {
		Company company1 = new Company();
		Company company2 = new Company();
		assertTrue(company1.equals(company2));
		company1.setCompanyName("foo");
		company1.setCompanyName("bar");
		assertFalse(company1.equals(company2));
		assertFalse(company1.equals(company2));
	}

	@Test
	public void testAddJob() {
		// test idempotency
		assertTrue(microsoft.getJobs().isEmpty());
		microsoft.getJobs().add(new Job("foo"));
		microsoft.getJobs().add(new Job("foo"));
		assertTrue(microsoft.getJobs().contains(new Job("foo")));
		assertTrue(microsoft.getJobs().size() == 1);
	}

}
