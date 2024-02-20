package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.CrudRepository;

import com.Jason.DevInternHubBackend.domain.Company;

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
		companyRepository.save(microsoft);
		companyRepository.save(google);
		companyRepository.save(meta);
		assertTrue(companyRepository.findByCompanyNameContainingIgnoreCase("O").size() == 2);
	}
	
	@Test
	public void testFindByCompanyNameIgnoreCase() {
		companyRepository.save(microsoft);
		assertTrue(companyRepository.findByCompanyNameIgnoreCase("mIcrosoft").isPresent());
	}
	
	@Test
	public void testSaveIdempotency() {
		companyRepository.save(new Company("foo", "url"));
		assertThrows(DataIntegrityViolationException.class, () -> {
            companyRepository.save(new Company("foo", "url")); // Attempt to save duplicate
        });
		assertThrows(DataIntegrityViolationException.class, () -> {
            companyRepository.save(new Company("foo", "bar"));
        });
		assertThrows(DataIntegrityViolationException.class, () -> {
            companyRepository.save(new Company("bar", "url"));
        });
	}
}
