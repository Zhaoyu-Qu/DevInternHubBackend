package com.Jason.DevInternHubBackend.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
