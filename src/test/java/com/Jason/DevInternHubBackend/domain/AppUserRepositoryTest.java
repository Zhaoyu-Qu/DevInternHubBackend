package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;

@SpringBootTest
public class AppUserRepositoryTest extends BaseRepositoryTest<AppUser, Long> {
	@Override
	public CrudRepository<AppUser, Long> getRepository() {
		return appUserRepository;
	}

	@Override
	public AppUser createEntity() {
		return new AppUser();
	}

	@Test
	public void testFindByUsernameIgnoreCase() {
		appUserRepository.save(new AppUser("foo", "bar", "admin"));
		assertTrue(appUserRepository.findByUsernameIgnoreCase("fOO").get().equals(new AppUser("foo", "bar", "admin")));
	}
	
	@Test
	public void testSaveDuplicate() {
		appUserRepository.save(new AppUser("foo", "bar", "admin"));
		assertThrows(DataIntegrityViolationException.class, () -> {
			appUserRepository.save(new AppUser("foo", "baz", "user"));
		});
	}
}
