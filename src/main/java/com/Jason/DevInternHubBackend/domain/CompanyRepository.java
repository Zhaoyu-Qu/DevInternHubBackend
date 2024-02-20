package com.Jason.DevInternHubBackend.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface CompanyRepository extends CrudRepository<Company, Long> {
	List<Company> findByCompanyNameContainingIgnoreCase(String partOfName);
	Optional<Company> findByCompanyNameIgnoreCase(String companyName);
}
