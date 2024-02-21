package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AppUserTest extends BaseTest {
	private AppUser sam;

	@BeforeEach
	protected void setUp() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();
		sam = new AppUser("Sam", "sampassword", "user");
	}

	@Test
	public void testEquals() {
		AppUser user1 = new AppUser();
		AppUser user2 = new AppUser();

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
			sam.setRole("foo");
		});

		// test valid string input
		assertDoesNotThrow(() -> {
			sam.setRole("admin");
		});
		assertTrue(sam.getRole().equals(Role.ADMIN));

		// test valid enum input
		assertDoesNotThrow(() -> {
			sam.setRole(Role.GUEST);
		});
		assertTrue(sam.getRole().equals(Role.GUEST));
	}

	@Test
	public void testAddOwnedJob() {
		// test idempotency
		sam.getOwnedJobs().add(new Job("foo"));
		sam.getOwnedJobs().add(new Job("foo"));
		assertTrue(sam.getOwnedJobs().size() == 1);
		assertTrue(sam.getOwnedJobs().contains(new Job("foo")));
	}

	@Test
	public void testAddBookmarkedJob() {
		// test idempotency
		sam.getBookmarkedJobs().add(new Job("foo"));
		sam.getBookmarkedJobs().add(new Job("foo"));
		assertTrue(sam.getBookmarkedJobs().size() == 1);
		assertTrue(sam.getBookmarkedJobs().contains(new Job("foo")));
	}
}
