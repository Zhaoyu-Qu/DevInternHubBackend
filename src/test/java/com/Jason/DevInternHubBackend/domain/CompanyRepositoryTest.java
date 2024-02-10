package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.repository.CrudRepository;

import com.Jason.DevInternHubBackend.domain.Company;

@DataJpaTest
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
}
