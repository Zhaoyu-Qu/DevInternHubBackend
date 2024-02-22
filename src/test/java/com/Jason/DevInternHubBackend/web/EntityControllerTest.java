package com.Jason.DevInternHubBackend.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.fasterxml.jackson.databind.JsonNode;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class EntityControllerTest extends BaseControllerTest {
	protected String entityNameLowerCasePlural; // if the entity is `Job` class, `entityNameLowerCasePlural` should be
												// `jobs`

	abstract void setEntityNameLowerCasePlural();

	@BeforeEach
	protected void entityControllerSetUp() throws Exception {
		// clear all repositories
		jobRepository.deleteAll();
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		
		// add pre-defined users
		appUserRepository
				.save(new AppUser(demoAdminUsername, new BCryptPasswordEncoder().encode(demoAdminPassword), "admin"));
		appUserRepository
				.save(new AppUser(demoUserUsername, new BCryptPasswordEncoder().encode(demoUserPassword), "user"));
		appUserRepository
				.save(new AppUser(demoGuestUsername, new BCryptPasswordEncoder().encode(demoGuestPassword), "guest"));
		// get an admin JWT token for all subsequent operations
		MvcResult mvcResult = mockMvc
				.perform(
						post("/login")
								.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}",
										demoAdminUsername, demoAdminPassword))
								.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		adminJwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		if (adminJwtToken != null && adminJwtToken.startsWith("Bearer")) {
			adminJwtToken = adminJwtToken.substring(6);
		} else {
			throw new Exception(String.format("retrieved adminJwtToken: %s is not valid.", adminJwtToken));
		}

		// get an user JWT token for all subsequent operations
		mvcResult = mockMvc
				.perform(
						post("/login")
								.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}",
										demoUserUsername, demoUserPassword))
								.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		userJwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		if (userJwtToken != null && userJwtToken.startsWith("Bearer")) {
			userJwtToken = userJwtToken.substring(6);
		} else {
			throw new Exception(String.format("retrieved userJwtToken: %s is not valid.", userJwtToken));
		}

		// get an guest JWT token for all subsequent operations
		mvcResult = mockMvc
				.perform(
						post("/login")
								.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}",
										demoGuestUsername, demoGuestPassword))
								.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		guestJwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		if (guestJwtToken != null && guestJwtToken.startsWith("Bearer")) {
			guestJwtToken = guestJwtToken.substring(6);
		} else {
			throw new Exception(String.format("retrieved guestJwtToken: %s is not valid.", guestJwtToken));
		}
		
		setEntityNameLowerCasePlural();
	}
}
