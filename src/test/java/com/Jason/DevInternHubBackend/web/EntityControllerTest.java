package com.Jason.DevInternHubBackend.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class EntityControllerTest extends BaseControllerTest {
	protected String entityNameLowerCasePlural; // if E is `Job` class, `entityNameLowerCasePlural` should be `jobs`
	protected ArrayList<String> sampleEntityPostBodies;
	protected ArrayList<String> sampleEntityPatchBodies;

	abstract void setSampleEntityPostBodies() throws Exception;

	abstract void setSampleEntityPatchBodies() throws Exception;

	abstract void setEntityNameLowerCasePlural();

	@BeforeEach
	protected void entityControllerSetUp() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();
		appUserRepository.save(new AppUser(demoAdminUsername, new BCryptPasswordEncoder().encode(demoAdminPassword), "admin"));
		appUserRepository.save(new AppUser(demoUserUsername, new BCryptPasswordEncoder().encode(demoUserPassword), "user"));
		appUserRepository.save(new AppUser(demoGuestUsername, new BCryptPasswordEncoder().encode(demoGuestPassword), "guest"));
		// get an admin JWT token for all subsequent operations
		MvcResult mvcResult = mockMvc
				.perform(post("/login").content(
						String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}", demoAdminUsername, demoAdminPassword))
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
				.perform(post("/login").content(
						String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}", demoUserUsername, demoUserPassword))
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
				.perform(post("/login").content(
						String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}", demoGuestUsername, demoGuestPassword))
						.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		guestJwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		if (guestJwtToken != null && guestJwtToken.startsWith("Bearer")) {
			guestJwtToken = guestJwtToken.substring(6);
		} else {
			throw new Exception(String.format("retrieved guestJwtToken: %s is not valid.", guestJwtToken));
		}
		sampleEntityPostBodies = new ArrayList<String>();
		sampleEntityPatchBodies = new ArrayList<String>();
		setSampleEntityPostBodies();
		setSampleEntityPatchBodies();
		setEntityNameLowerCasePlural();
	}

	@Test
	protected void testAdminCrudOperations() throws Exception {
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(adminJwtToken);
		
		// make sure repository is initially empty
		String urlForGetAll = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andExpect(status().isOk())
				.andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assert rootNode.path("_embedded").path(entityNameLowerCasePlural).isEmpty();

		// post one entity
		String urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(sampleEntityPostBodies.get(0)))
				.andExpect(status().is2xxSuccessful()).andReturn();

		// confirm the entity is created
		String location = mvcResult.getResponse().getHeader("location");
		mockMvc.perform(get(location).headers(headers)).andExpect(status().isOk());

		// test put method
		mockMvc.perform(put(location).headers(headers).content(sampleEntityPostBodies.get(1)))
				.andExpect(status().is2xxSuccessful());

		// test patch method
		mockMvc.perform(patch(location).headers(headers).content(sampleEntityPatchBodies.get(1)))
				.andExpect(status().is2xxSuccessful());

		// test delete method
		mockMvc.perform(delete(location).headers(headers)).andExpect(status().is2xxSuccessful());
		mockMvc.perform(delete(location).headers(headers)).andExpect(status().is4xxClientError());
	}
	
	@Test
	protected void testUserCrudOperations() throws Exception {
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(userJwtToken);
		
		// make sure repository is initially empty
		String urlForGetAll = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andExpect(status().isOk())
				.andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assert rootNode.path("_embedded").path(entityNameLowerCasePlural).isEmpty();

		// post one entity
		String urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(sampleEntityPostBodies.get(0)))
				.andExpect(status().is2xxSuccessful()).andReturn();

		// confirm the entity is created
		String location = mvcResult.getResponse().getHeader("location");
		mockMvc.perform(get(location).headers(headers)).andExpect(status().isOk());

		// test put method
		mockMvc.perform(put(location).headers(headers).content(sampleEntityPostBodies.get(1)))
				.andExpect(status().is2xxSuccessful());

		// test patch method
		mockMvc.perform(patch(location).headers(headers).content(sampleEntityPatchBodies.get(1)))
				.andExpect(status().is2xxSuccessful());

		// test delete method
		mockMvc.perform(delete(location).headers(headers)).andExpect(status().is2xxSuccessful());
		mockMvc.perform(delete(location).headers(headers)).andExpect(status().is4xxClientError());
	}
	
	@Test
	protected void testGuestCrudOperations() throws Exception {
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(guestJwtToken);
		
		// test http get request (get all records)
		String urlForGetAll = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andExpect(status().isOk())
				.andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assert rootNode.path("_embedded").path(entityNameLowerCasePlural).isEmpty();

		// post one entity with guest role
		String urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(sampleEntityPostBodies.get(0)))
				.andExpect(status().is4xxClientError()).andReturn();
		
		// post one entity with admin role
		headers.setBearerAuth(adminJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(sampleEntityPostBodies.get(0)))
				.andExpect(status().is2xxSuccessful()).andReturn();

		// confirm the entity is created
		headers.setBearerAuth(guestJwtToken);
		String location = mvcResult.getResponse().getHeader("location");
		mockMvc.perform(get(location).headers(headers)).andExpect(status().isOk());

		// test put method
		mockMvc.perform(put(location).headers(headers).content(sampleEntityPostBodies.get(1)))
				.andExpect(status().is4xxClientError());

		// test patch method
		mockMvc.perform(patch(location).headers(headers).content(sampleEntityPatchBodies.get(1)))
				.andExpect(status().is4xxClientError());

		// test delete method
		mockMvc.perform(delete(location).headers(headers)).andExpect(status().is4xxClientError());
	}
}
