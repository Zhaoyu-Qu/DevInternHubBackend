package com.Jason.DevInternHubBackend.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CompanyRepository extends CrudRepository<Company, Long> {
	List<Company> findByCompanyNameContainingIgnoreCase(@Param("companyName") String partOfName);
}
