package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.CrudRepository;

@DataJpaTest
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
		appUserRepository.save(sam);
		assertTrue(appUserRepository.findByUsernameIgnoreCase("sAm").get().equals(sam));
	}
}
