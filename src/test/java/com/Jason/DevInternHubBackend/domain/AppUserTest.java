package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
