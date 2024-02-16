package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
public class AppUserTest extends BaseTest {
	@Test
	public void testEquals() {
		AppUser user1 = new AppUser();
		AppUser user2 = new AppUser();
		assertTrue(user1.equals(user2));

		user1.setUsername("Clark");
		user2.setUsername("Jane");
		assertFalse(user1.equals(user2));

		user2.setUsername("Clark");
		assertTrue(user1.equals(user2));

		user1.setPassword("foo");
		user2.setPassword("bar");
		assertTrue(user1.equals(user2));
	}

	@Test
	public void testSetRole() {
		// test invalid input
		assertThrows(IllegalArgumentException.class, () -> {
			this.sam.setRole("foo");
		});

		// test valid string input
		assertDoesNotThrow(() -> {
			this.sam.setRole("admin");
		});
		assertTrue(sam.getRole().equals(Role.ADMIN));

		// test valid enum input
		assertDoesNotThrow(() -> {
			this.sam.setRole(Role.GUEST);
		});
		assertTrue(sam.getRole().equals(Role.GUEST));
	}
	
	@Test
	public void testAddOwnedJob() {
		// test adding owned job for the first time
		assertTrue(sam.getOwnedJobs().size() == 0);
		assertTrue(backendJob.getOwner() == null);
		sam.addOwnedJob(backendJob);
		assertTrue(sam.getOwnedJobs().size() == 1);
		assertTrue(sam.getOwnedJobs().contains(backendJob));
		assertTrue(backendJob.getOwner().equals(sam));
		
		// test idempotency
		sam.addOwnedJob(backendJob);
		assertTrue(sam.getOwnedJobs().size() == 1);
		assertTrue(sam.getOwnedJobs().contains(backendJob));
		assertTrue(backendJob.getOwner().equals(sam));
		
		// test adding a second owned job
		sam.addOwnedJob(frontendJob);
		assertTrue(sam.getOwnedJobs().size() == 2);
		assertTrue(sam.getOwnedJobs().contains(backendJob));
		assertTrue(sam.getOwnedJobs().contains(frontendJob));
		assertTrue(backendJob.getOwner().equals(sam));
		assertTrue(frontendJob.getOwner().equals(sam));
		
		// test changing ownership
		jack.addOwnedJob(frontendJob);
		assertTrue(sam.getOwnedJobs().size() == 1);
		assertTrue(jack.getOwnedJobs().size() == 1);
		assertTrue(frontendJob.getOwner().equals(jack));
		
	}
	
	@Test
	public void testAddBookmarkedJob() {
		assertTrue(sam.getBookmarkedJobs().size() == 0);
		assertTrue(backendJob.getBookmarkHolders().size() == 0);
		
		// test adding a job
		sam.addBookmarkedjob(backendJob);
		assertTrue(sam.getBookmarkedJobs().contains(backendJob));
		assertTrue(backendJob.getBookmarkHolders().contains(sam));
		
		// test idempotency
		sam.addBookmarkedjob(backendJob);
		assertTrue(sam.getBookmarkedJobs().contains(backendJob));
		assertTrue(sam.getBookmarkedJobs().size() == 1);
		assertTrue(backendJob.getBookmarkHolders().size() == 1);
		
		// test adding a second job
		sam.addBookmarkedjob(frontendJob);
		assertTrue(sam.getBookmarkedJobs().contains(frontendJob));
		assertTrue(sam.getBookmarkedJobs().size() == 2);
		assertTrue(backendJob.getBookmarkHolders().size() == 1);
		assertTrue(frontendJob.getBookmarkHolders().size() == 1);
		
		// test different user adding same job
		jack.addBookmarkedjob(backendJob);
		assertTrue(sam.getBookmarkedJobs().contains(frontendJob));
		assertTrue(jack.getBookmarkedJobs().contains(backendJob));
		assertTrue(sam.getBookmarkedJobs().size() == 2);
		assertTrue(jack.getBookmarkedJobs().size() == 1);
		assertTrue(backendJob.getBookmarkHolders().size() == 2);
		assertTrue(backendJob.getBookmarkHolders().containsAll(Arrays.asList(sam, jack)));
		assertTrue(frontendJob.getBookmarkHolders().size() == 1);
		assertTrue(frontendJob.getBookmarkHolders().contains(sam));
	}
	
	@Test
	public void testRemoveBookmarkedJob() {
		sam.addBookmarkedjob(backendJob);
		sam.removeBookmarkedJob(backendJob);
		assertTrue(sam.getBookmarkedJobs().size() == 0);
		assertTrue(backendJob.getBookmarkHolders().size() == 0);
	}
}
