package com.Jason.DevInternHubBackend.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@TestPropertySource(locations = "classpath:inMemoryDatabase.properties")
public abstract class BaseTest {
	@Autowired
	protected CompanyRepository companyRepository;
	@Autowired
	protected AppUserRepository appUserRepository;
	@Autowired
	protected TechnologyRepository technologyRepository;
	@Autowired
	protected JobRepository jobRepository;
	@Autowired
	protected TransactionTemplate transactionTemplate;
	@PersistenceContext
	protected EntityManager entityManager;
}
