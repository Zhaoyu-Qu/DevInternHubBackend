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

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.Company;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.Technology;
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
		assertTrue(rootNode.isEmpty());

		// test non-empty Job repository
		Job job = new Job();
		job.setTitle("foo");
		job.setUrl("bar");
		jobRepository.save(job);
		mvcResult = mockMvc.perform(get(urlForGetAll).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertFalse(rootNode.isEmpty());
		assertTrue(rootNode.get(0).get("title").asText().equals("foo"));
		assertTrue(rootNode.get(0).get("url").asText().equals("bar"));
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
		job.setUrl("bar");
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
		assertTrue(rootNode.get("url").asText().equals("bar"));
	}
	
	@Test
	private void testPostJob() throws Exception {
		testPostJob(adminJwtToken);
		testPostJob(userJwtToken);
		testPostJob(guestJwtToken);
		testPostJob("");
	}
	
	private void testPostJob(String jwtToken) throws Exception {
		// postBody1 has all correct inputs
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
				+ "    \"foo\",\n"
				+ "    \"bar\"\n"
				+ "  ]\n"
				+ "}";
		// postBody2 has dates and type of wrong formats
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
				+ "    \"bar\",\n"
				+ "    \"baz\"\n"
				+ "  ]\n"
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
		
		// examine retrieved results of primitive types
		responseString1 = mvcResult1.getResponse().getContentAsString();
		responseString2 = mvcResult2.getResponse().getContentAsString();
		rootNode1 = objectMapper.readTree(responseString1);
		rootNode2 = objectMapper.readTree(responseString2);
		assertTrue(rootNode1.get("title").asText().equals("title1"));
		assertTrue(rootNode1.get("openingDate").asText().equals("2024-02-05"));
		assertTrue(rootNode1.get("closingDate").asText().equals("2024-02-09"));
		assertTrue(rootNode1.get("description").asText().equals("description1"));
		assertTrue(rootNode1.get("url").asText().equals("url1"));
		assertTrue(rootNode1.get("location").asText().equals("location1"));
		assertTrue(rootNode1.get("companyName").asText().equals("companyName1"));
		assertTrue(rootNode1.get("specialisation").asText().equals("specialisation1"));
		assertTrue(rootNode1.get("type").asText().equals("Graduate Job"));
		JsonNode technologiesNode1 = rootNode1.get("technologies");
		assertTrue(technologiesNode1.isArray());
		
		// examine related resources
		Job job1 = jobRepository.findById(rootNode1.get("id").asLong()).get();
		for (JsonNode n : technologiesNode1) {
			assertTrue(n.asText().equals("foo") || n.asText().equals("bar"));
			assertTrue(technologyRepository.existsByNameIgnoreCase(n.asText()));
			Technology technology = technologyRepository.findByNameIgnoreCase(n.asText()).get();
			assertTrue(job1.getTechnologies().contains(technology));
			technology.getJobs().contains(job1);
			
		}
		AppUser owner1 = job1.getOwner();
		assertTrue(appUserRepository.existsById(owner1.getId()));
		assertTrue(owner1.getOwnedJobs().contains(job1));
		Company company1 = job1.getCompany();
		assertTrue(companyRepository.existsById(company1.getId()));
		assertTrue(company1.getJobs().contains(job1));
		
		// examine retrieved results of primitive types
		assertTrue(rootNode2.get("title").asText().equals("title2"));
		assertTrue(rootNode2.get("openingDate").asText() == null);
		assertTrue(rootNode2.get("closingDate").asText() == null);
		assertTrue(rootNode2.get("description").asText().equals("description2"));
		assertTrue(rootNode2.get("url").asText().equals("url2"));
		assertTrue(rootNode2.get("location").asText().equals("location2"));
		assertTrue(rootNode2.get("companyName").asText().equals("companyName2"));
		assertTrue(rootNode2.get("specialisation").asText().equals("specialisation2"));
		assertTrue(rootNode2.get("type") == null);
		JsonNode technologiesNode2 = rootNode2.get("technologies");
		assertTrue(technologiesNode2.isArray());
		// examine related resources
		Job job2 = jobRepository.findById(rootNode2.get("id").asLong()).get();
		for (JsonNode n : technologiesNode2) {
			assertTrue(n.asText().equals("baz") || n.asText().equals("bar"));
			assertTrue(technologyRepository.existsByNameIgnoreCase(n.asText()));
			Technology technology = technologyRepository.findByNameIgnoreCase(n.asText()).get();
			assertTrue(job2.getTechnologies().contains(technology));
			technology.getJobs().contains(job2);
			
		}
		AppUser owner2 = job2.getOwner();
		assertTrue(appUserRepository.existsById(owner2.getId()));
		assertTrue(owner2.getOwnedJobs().contains(job2));
		assertTrue(owner1.getId().equals(owner2.getId()));
		Company company2 = job1.getCompany();
		assertTrue(companyRepository.existsById(company2.getId()));
		assertTrue(company2.getJobs().contains(job2));
		
		if (jwtToken.equals(adminJwtToken)) {
			assertTrue(rootNode1.get("isVerified").asBoolean());
			assertTrue(rootNode2.get("isVerified").asBoolean());
		} else {
			assertFalse(rootNode1.get("isVerified").asBoolean());
			assertFalse(rootNode2.get("isVerified").asBoolean());
		}
	}
	private void testPatchJob() throws Exception {
	}
}
