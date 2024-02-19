package com.Jason.DevInternHubBackend.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.Test;

import com.Jason.DevInternHubBackend.domain.Job;
import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
public class JobControllerTest extends EntityControllerTest {
	@Override
	void setEntityNameLowerCasePlural() {
		entityNameLowerCasePlural = English.plural(Job.class.getSimpleName()).toLowerCase();
	}

	@Test
	public void testGetAllJobs() throws Exception {
		testGetAllJobs(adminJwtToken);
		testGetAllJobs(userJwtToken);
		testGetAllJobs(guestJwtToken);
		testGetAllJobs("");
	}

	private void testGetAllJobs(String jwtToken) throws Exception {
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		String urlForGetAll = restBaseApi + "/" + entityNameLowerCasePlural;

		// test empty Job Repository
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		jobRepository.deleteAll();
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assert rootNode.isEmpty();

		// test non-empty Job repository
		jobRepository.save(new Job("some job"));
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assert !rootNode.isEmpty();
		assertTrue(rootNode.get(0).get("title").asText().equals("some job"));
	}

	@Test
	public void testGetJob() throws Exception {
		testGetJob(adminJwtToken);
		testGetJob(userJwtToken);
		testGetJob(guestJwtToken);
		testGetJob("");
	}

	private void testGetJob(String jwtToken) throws Exception {
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// save a `Job` resource into the database
		jobRepository.deleteAll();
		Job job = new Job("some job");
		jobRepository.save(job);
		Long resourceId = job.getId();
		String urlForGet = restBaseApi + "/" + entityNameLowerCasePlural + "/" + resourceId;
		assertTrue(jobRepository.findById(resourceId).isPresent());

		// get the resource
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		mvcResult = mockMvc.perform(get(urlForGet).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("title").asText().equals("some job"));
	}
	
	@Test
	private void testPostJob() throws Exception {
		testPostJob(adminJwtToken);
		testPostJob(userJwtToken);
		testPostJob(guestJwtToken);
		testPostJob("");
	}
	
	private void testPostJob(String jwtToken) throws Exception {
		// postBody1 has all correct inputs, except `verified` which is meant to be ignored
		String postBody1 = "{\n"
				+ "  \"title\": \"title1\",\n"
				+ "  \"description\": \"description1\",\n"
				+ "  \"url\": \"url1\",\n"
				+ "  \"location\": \"location1\",\n"
				+ "  \"companyName\": \"companyName1\",\n"
				+ "  \"openingDate\": \"2024-02-05\",\n"
				+ "  \"closingDate\": \"2024-02-09\",\n"
				+ "  \"specialisation\": \"specialisation1\",\n"
				+ "  \"type\": \"Graduate Job\",\n"
				+ "  \"technologies\": [\n"
				+ "    {\n"
				+ "      \"name\": \"Technology11\",\n"
				+ "      \"name\": \"Technology12\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"verified\": true\n"
				+ "}";
		// postBody2 has dates of the wrong formats and the `verified` property which is meant to be ignored
		String postBody2 = "{\n"
				+ "  \"title\": \"title2\",\n"
				+ "  \"description\": \"description2\",\n"
				+ "  \"url\": \"url2\",\n"
				+ "  \"location\": \"location2\",\n"
				+ "  \"companyName\": \"companyName2\",\n"
				+ "  \"openingDate\": \"05/02/2024\",\n"
				+ "  \"closingDate\": \"09/02/2024\",\n"
				+ "  \"specialisation\": \"specialisation2\",\n"
				+ "  \"type\": \"type2\",\n"
				+ "  \"technologies\": [\n"
				+ "    {\n"
				+ "      \"name\": \"Technology21\",\n"
				+ "      \"name\": \"Technology22\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "  \"verified\": false\n"
				+ "}";
		String urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		String responseString1, responseString2;
		String location1, location2;
		MvcResult mvcResult1, mvcResult2;
		JsonNode rootNode1, rootNode2;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		// post resources
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		mvcResult1 = mockMvc.perform(post(urlForPost).headers(headers).content(postBody1)).andReturn();
		mvcResult2 = mockMvc.perform(post(urlForPost).headers(headers).content(postBody2)).andReturn();
		
		// only ADMIN and USER may make post requests
		if (!jwtToken.equals(adminJwtToken) && !jwtToken.equals(userJwtToken)) {
			assertTrue(mvcResult1.getResponse().getStatus() == 401);
			assertTrue(mvcResult2.getResponse().getStatus() == 401);
			return;
		}
		
		// retrieve the saved resources using HTTP GET
		location1 = mvcResult1.getResponse().getHeader("location");
		location2 = mvcResult1.getResponse().getHeader("location");
		mvcResult1 = mockMvc.perform(get(location1).headers(headers)).andExpect(status().isOk()).andReturn();
		mvcResult2 = mockMvc.perform(get(location2).headers(headers)).andExpect(status().isOk()).andReturn();
		
		// examine retrieved results
		responseString1 = mvcResult1.getResponse().getContentAsString();
		responseString2 = mvcResult2.getResponse().getContentAsString();
		rootNode1 = objectMapper.readTree(responseString1);
		rootNode2 = objectMapper.readTree(responseString2);
		assertTrue(rootNode1.get("title").asText().equals("title1"));
		assertTrue(rootNode2.get("title").asText().equals("title2"));
		assertTrue(rootNode1.get("openingDate").asText().equals("2024-02-05"));
		assertTrue(rootNode2.get("openingDate").asText() == null);
		assertTrue(rootNode1.get("closingDate").asText().equals("2024-02-09"));
		assertTrue(rootNode2.get("closingDate").asText() == null);
		assertTrue(rootNode1.get("description").asText().equals("description1"));
		assertTrue(rootNode2.get("description").asText().equals("description2"));
		assertTrue(rootNode1.get("url").asText().equals("url1"));
		assertTrue(rootNode2.get("url").asText().equals("url2"));
		assertTrue(rootNode1.get("location").asText().equals("location1"));
		assertTrue(rootNode2.get("location").asText().equals("location2"));
		assertTrue(rootNode1.get("companyName").asText().equals("companyName1"));
		assertTrue(rootNode2.get("companyName").asText().equals("companyName2"));
		assertTrue(rootNode1.get("specialisation").asText().equals("specialisation1"));
		assertTrue(rootNode2.get("specialisation").asText().equals("specialisation2"));
		assertTrue(rootNode1.get("type").asText().equals("Graduate Job"));
		assertTrue(rootNode2.get("type") == null);
		if (jwtToken.equals(adminJwtToken)) {
			assertTrue(rootNode1.get("verified").asBoolean());
			assertTrue(rootNode2.get("verified").asBoolean());
		} else {
			assertFalse(rootNode1.get("verified").asBoolean());
			assertFalse(rootNode2.get("verified").asBoolean());
		}
	}
	private void testPatchJob() throws Exception {
		String responseString, location;
		MvcResult mvcResult;
		JsonNode rootNode;
		HttpHeaders headers = new HttpHeaders();
		String urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String postBody1 = "{\n"
				+ "  \"title\": \"title1\",\n"
				+ "  \"description\": \"description1\",\n"
				+ "  \"url\": \"url1\",\n"
				+ "  \"location\": \"location1\",\n"
				+ "  \"companyName\": \"companyName1\",\n"
				+ "  \"openingDate\": \"2024-02-05\",\n"
				+ "  \"closingDate\": \"2024-02-09\",\n"
				+ "  \"specialisation\": \"specialisation1\",\n"
				+ "  \"type\": \"Graduate Job\",\n"
				+ "  \"technologies\": [\n"
				+ "    {\n"
				+ "      \"name\": \"Technology11\",\n"
				+ "      \"name\": \"Technology12\"\n"
				+ "    }\n"
				+ "  ],\n"
				+ "}";
		
		// test admin behavior
		headers.setBearerAuth(adminJwtToken);
		// save a resource first
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody1)).andExpect(status().isOk()).andReturn();
		location = mvcResult.getResponse().getHeader("location");
		// then, update the resource using the patch method 
		mockMvc.perform(patch(location).headers(headers).content(
				"{\n"
				+ "  \"title\": \"title2\",\n"
				+ "  \"verified\": false\n"
				+ "}"
				)).andExpect(status().isOk());
		// retrieve the updated resource and examine its properties
		mvcResult = mockMvc.perform(get(location).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("title").asText().equals("title2"));
		assertFalse(rootNode.get("verified").asBoolean());
		
		// test user behaviour
		headers.setBearerAuth(userJwtToken);
		// a non-admin user shouldn't be able to modify others' resources
		mockMvc.perform(patch(location).headers(headers).content(
				"{\n"
				+ "  \"title\": \"title2\",\n"
				+ "  \"verified\": false\n"
				+ "}"
				)).andExpect(status().isForbidden());
		// create a new resource with user privilege
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody1)).andExpect(status().isOk()).andReturn();
		location = mvcResult.getResponse().getHeader("location");
		// make a patch request
		mockMvc.perform(patch(location).headers(headers).content(
				"{\n"
				+ "  \"title\": \"title3\",\n"
				+ "  \"verified\": true\n"
				+ "}"
				)).andExpect(status().isOk());
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("title").asText().equals("title3"));
		// the `verified` property in the patch request should have been ignored by the backend
		assertFalse(rootNode.get("verified").asBoolean());
		
		// admin can verify a posting
		headers.setBearerAuth(adminJwtToken);
		mockMvc.perform(patch(location).headers(headers).content(
				"{\n"
				+ "  \"verified\": true\n"
				+ "}"
				)).andExpect(status().isOk());
		assertTrue(rootNode.get("verified").asBoolean());
		
		// test patch a non-existent resource
		String urlForPatchNonExistentResource = restBaseApi + "/" + entityNameLowerCasePlural + "/100";
		headers.setBearerAuth(adminJwtToken);
		mockMvc.perform(patch(urlForPatchNonExistentResource).headers(headers).content(
				"{\n"
				+ "  \"title\": \"title3\",\n"
				+ "  \"verified\": true\n"
				+ "}"
				)).andExpect(status().isNotFound());
		
		// test guest user behaviour
		headers.setBearerAuth(guestJwtToken);
		mockMvc.perform(patch(location).headers(headers).content(
				"{\n"
				+ "  \"title\": \"title3\",\n"
				+ "}"
				)).andExpect(status().isForbidden());
		
		headers.setBearerAuth(null);
		mockMvc.perform(patch(location).headers(headers).content(
				"{\n"
				+ "  \"title\": \"title3\",\n"
				+ "}"
				)).andExpect(status().isForbidden());
	}
}
