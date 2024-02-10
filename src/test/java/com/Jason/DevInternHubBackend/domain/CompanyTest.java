package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class CompanyTest extends BaseTest {
	@Test
	public void testEquals() {
		Company company1 = new Company();
		Company company2 = new Company();
		assertTrue(company1.equals(company2));
		company1.addJob(backendJob);
		assertFalse(company1.equals(company2));
	}

	@Test
	public void testAddJob() {
		assertTrue(microsoft.getJobs().isEmpty());
		assertTrue(backendJob.getCompany() == null);
		microsoft.addJob(backendJob);
		assertTrue(microsoft.getJobs().contains(backendJob));
		assertTrue(backendJob.getCompany().equals(microsoft));
	}
	
	@Test
	public void testCascading() {
		// initial condition: empty repositories
		assertTrue(companyRepository.count() == 0);
		assertTrue(jobRepository.count() == 0);
		
		// save a company object and the associated Technology objects will be written to the
		// database too
		transactionTemplate.execute(status -> {
			microsoft.addJob(backendJob);
			companyRepository.save(microsoft);
			return null;
		});
		assertTrue(jobRepository.count() == 1);
		assertTrue(companyRepository.count() == 1);

		
		// retrieve the saved Job and examine if it is the same as the one we have in memory
		// use transactionTemplate.execute to make sure these statements are executed within the 
		// same transaction context. The use of transactionTemplate.execute is optional here, but
		// may prevent a potential LazyInitializationException if more code is added
		transactionTemplate.execute(status -> {
			Job retrievedJobObject = jobRepository.findById(backendJob.getId()).get();
			assertTrue(retrievedJobObject.equals(backendJob));
			assertTrue(retrievedJobObject.getCompanyName().equals(microsoft.getCompanyName()));
			return null;
		});

		
		// deleting a Company record won't affect Technologies
		companyRepository.deleteById(microsoft.getId());
		assertTrue(companyRepository.count() == 0);
		assertTrue(companyRepository.count() == 0);
	}
}
