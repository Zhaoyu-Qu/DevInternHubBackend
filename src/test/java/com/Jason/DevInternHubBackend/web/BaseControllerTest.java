package com.Jason.DevInternHubBackend.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.Jason.DevInternHubBackend.domain.AppUserRepository;
import com.Jason.DevInternHubBackend.domain.CompanyRepository;
import com.Jason.DevInternHubBackend.domain.JobRepository;
import com.Jason.DevInternHubBackend.domain.TechnologyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestPropertySource(locations = "classpath:inMemoryDatabase.properties")
@AutoConfigureMockMvc
public abstract class BaseControllerTest {
	@Autowired
	protected CompanyRepository companyRepository;
	@Autowired
	protected AppUserRepository appUserRepository;
	@Autowired
	protected TechnologyRepository technologyRepository;
	@Autowired
	protected JobRepository jobRepository;
	@Autowired
	protected MockMvc mockMvc;
	@Autowired
	protected ObjectMapper objectMapper; // ObjectMapper is used for parsing JSON
	@Value("${spring.data.rest.basePath}")
	protected String restBaseApi;
	@Value("${test.username}")
	protected String testUsername;
	@Value("${test.password}")
	protected String testPassword;
	protected String jwtToken;

}
