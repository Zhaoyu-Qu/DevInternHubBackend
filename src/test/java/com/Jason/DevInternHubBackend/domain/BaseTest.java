package com.Jason.DevInternHubBackend.domain;

import org.junit.jupiter.api.BeforeEach;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;
import com.Jason.DevInternHubBackend.domain.Company;
import com.Jason.DevInternHubBackend.domain.CompanyRepository;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.JobRepository;
import com.Jason.DevInternHubBackend.domain.Technology;
import com.Jason.DevInternHubBackend.domain.TechnologyRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@TestPropertySource(locations = "classpath:inMemoryDatabase.properties")
public abstract class BaseTest {
	protected Company microsoft, google, meta;
	protected Job backendJob, frontendJob, fullStackJob;
	protected AppUser sam, jack, luke;
	protected Technology java, python, spring;
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

	@BeforeEach
	protected void setUp() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();

		microsoft = new Company("Microsoft", "www.microsoft.com");
		google = new Company("Google", "www.google.com");
		meta = new Company("Meta", "www.meta.com");

		backendJob = new Job("Backend Job");
		backendJob.setUrl("www.foo.com");
		frontendJob = new Job("Frontend Job");
		frontendJob.setUrl("www.bar.com");
		fullStackJob = new Job("Full Stack Job");
		fullStackJob.setUrl("www.baz.com");

		sam = new AppUser("Sam", "sampassword", "user");
		jack = new AppUser("Jack", "jackpassword", "user");
		luke = new AppUser("Luke", "lukepassword", "user");

		java = new Technology("Java");
		python = new Technology("Python");
		spring = new Technology("Spring");
	}
}
