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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
		appUserRepository.save(new AppUser(testUsername, new BCryptPasswordEncoder().encode(testPassword), "admin"));
		// get a JWT token for all subsequent operations
		MvcResult mvcResult = mockMvc
				.perform(post("/login").content(
						String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}", testUsername, testPassword))
						.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andDo(print()).andExpect(status().isOk()).andReturn();
		jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		if (jwtToken != null && jwtToken.startsWith("Bearer")) {
			jwtToken = jwtToken.substring(6);
		}
		sampleEntityPostBodies = new ArrayList<String>();
		sampleEntityPatchBodies = new ArrayList<String>();
		setSampleEntityPostBodies();
		setSampleEntityPatchBodies();
		setEntityNameLowerCasePlural();
	}

	// test `get all entities`, `get one entity`, `post`, `put`, `delete`, `patch`
	@Test
	protected void testEntityControllerApis() throws Exception {
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		// make sure repository is initially empty
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(jwtToken);

		String urlForGetAll = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andDo(print()).andExpect(status().isOk())
				.andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assert rootNode.path("_embedded").path(entityNameLowerCasePlural).isEmpty();

		// post one entity
		String urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(sampleEntityPostBodies.get(0)))
				.andDo(print()).andExpect(status().is2xxSuccessful()).andReturn();

		// confirm the entity is created
		String location = mvcResult.getResponse().getHeader("location");
		mockMvc.perform(get(location).headers(headers)).andDo(print()).andExpect(status().isOk());

		// test put method
		mockMvc.perform(put(location).headers(headers).content(sampleEntityPostBodies.get(1))).andDo(print())
				.andExpect(status().is2xxSuccessful());

		// test patch method
		mockMvc.perform(patch(location).headers(headers).content(sampleEntityPatchBodies.get(1))).andDo(print())
				.andExpect(status().is2xxSuccessful());

		// test delete method
		mockMvc.perform(delete(location).headers(headers)).andDo(print()).andExpect(status().is2xxSuccessful());
		mockMvc.perform(delete(location).headers(headers)).andDo(print()).andExpect(status().is4xxClientError());

	}
}
