package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.Technology;

@SpringBootTest
public class JobTest extends BaseTest {
	@Test
	public void testEquals() {
		Job job1 = new Job();
		Job job2 = new Job();
		assertTrue(job1.equals(job2));
		job1.setCompany(microsoft);
		assertFalse(job1.equals(job2));
		job2.setCompany(microsoft);
		assertTrue(job1.equals(job2));
		job1.setTitle("foo");
		assertFalse(job1.equals(job2));
		job2.setTitle("foo");
		assertTrue(job1.equals(job2));
	}

	@Test
	public void testSetCompany() {
		assertTrue(backendJob.getCompany() == null);
		assertFalse(microsoft.getJobs().contains(backendJob));
		backendJob.setCompany(microsoft);
		assertTrue(backendJob.getCompany().equals(microsoft));
		assertTrue(microsoft.getJobs().contains(backendJob));
		assertTrue(backendJob.getCompanyName().equals(microsoft.getCompanyName()));
	}
	
	@Test
	public void testSetOwner() {
		// test assigning owner
		assertTrue(backendJob.getOwner() == null);
		assertTrue(sam.getJobs().isEmpty());
		backendJob.setOwner(sam);
		assertTrue(backendJob.getOwner().equals(sam));
		assertTrue(sam.getJobs().contains(backendJob));
		
		// test idempotency
		backendJob.setOwner(sam);
		assertTrue(backendJob.getOwner().equals(sam));
		assertTrue(sam.getJobs().contains(backendJob));
		assertTrue(sam.getJobs().size() == 1);
		
		// test changing owner
		backendJob.setOwner(jack);
		assertFalse(sam.getJobs().contains(backendJob));
		assertTrue(jack.getJobs().contains(backendJob));
		assertTrue(jack.getJobs().size() == 1);
		assertTrue(backendJob.getOwner().equals(jack));
		
		// test assigning owner to a second job
		frontendJob.setOwner(jack);
		assertTrue(jack.getJobs().size() == 2);
		assertTrue(jack.getJobs().contains(frontendJob));
		assertTrue(frontendJob.getOwner().equals(jack));
	}

	@Test
	public void testCascading() {
		// initial condition: empty repositories
		assertTrue(technologyRepository.count() == 0);
		assertTrue(jobRepository.count() == 0);
		
		// save a Job object and the associated Technology objects will be written to the
		// database too
		backendJob.addTechnology(java);
		jobRepository.save(backendJob);
		assertTrue(jobRepository.count() == 1);
		assertTrue(technologyRepository.count() == 1);
		
		// retrieve the saved Technology and examine if it is the same as the one we have in memory
		// use transactionTemplate.execute to make sure these statements are executed within the 
		// same transaction context. The use of transactionTemplate.execute is optional here, but
		// may prevent a potential LazyInitializationException if more code is added
		transactionTemplate.execute(status -> {
			Technology retrievedTechnologyObject = technologyRepository.findById(java.getId()).get();
			assertTrue(retrievedTechnologyObject.equals(java));
			return null;
		});

		
		// deleting a Company record won't affect Technologies
		jobRepository.deleteById(backendJob.getId());
		assertTrue(technologyRepository.count() == 1);
		assertTrue(jobRepository.count() == 0);
	}
}
