package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;

@SpringBootTest
public class CompanyRepositoryTest extends BaseRepositoryTest<Company, Long> {
	@Override
	public CrudRepository<Company, Long> getRepository() {
		return companyRepository;
	}

	@Override
	public Company createEntity() {
		return new Company();
	}

	@Test
	public void testFindByCompanyNameContainingIgnoreCase() {
		companyRepository.save(new Company("Microsoft"));
		companyRepository.save(new Company("Google"));
		companyRepository.save(new Company("Meta"));
		assertTrue(companyRepository.findByCompanyNameContainingIgnoreCase("O").size() == 2);
	}

	@Test
	public void testFindByCompanyNameIgnoreCase() {
		companyRepository.save(new Company("Microsoft"));
		assertTrue(companyRepository.findByCompanyNameIgnoreCase("mIcrosoft").isPresent());
	}

	@Test
	public void testSaveDuplicate() {
		companyRepository.save(new Company("foo", "url"));
		assertThrows(DataIntegrityViolationException.class, () -> {
			companyRepository.save(new Company("foo", "url"));
		});
		assertThrows(DataIntegrityViolationException.class, () -> {
			companyRepository.save(new Company("foo", "bar"));
		});
		assertThrows(DataIntegrityViolationException.class, () -> {
			companyRepository.save(new Company("bar", "url"));
		});
	}
}
