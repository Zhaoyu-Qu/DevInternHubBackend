package com.Jason.DevInternHubBackend.web;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.SSLEngineResult.Status;

import org.atteo.evo.inflector.English;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.Company;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.Technology;
import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
public class JobControllerTest extends EntityControllerTest {
	private String urlForPost;
	private String urlForGetAll;
	HttpHeaders headers;
	// postBody has all correct inputs
	String postBody = "{\n"
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
	
	@BeforeEach
	public void setUpEnvironment() {
		urlForPost = restBaseApi + "/" + entityNameLowerCasePlural;
		urlForGetAll = urlForPost;
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
	}
	
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

	public void testGetAllJobs(String jwtToken) throws Exception {
		// setup
		jobRepository.deleteAll();
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);

		// test empty Job Repository
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

	public void testGetJob(String jwtToken) throws Exception {
		// setup
		jobRepository.deleteAll();
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);

		// save a `Job` resource into the database
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
	public void testPostJob() throws Exception {
		testPostJob(adminJwtToken);
		testPostJob(userJwtToken);
		testPostJob(guestJwtToken);
		testPostJob("");
	}
	
	public void testPostJob(String jwtToken) throws Exception {
		// setup
		jobRepository.deleteAll();
		String responseString, location;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);

		// post a resource
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andReturn();
		int statusCode = mvcResult.getResponse().getStatus();
		if (jwtToken.equals(adminJwtToken) || jwtToken.equals(userJwtToken)) {
			assertTrue(statusCode == 201);
			location = mvcResult.getResponse().getHeader("location");
		} else {
			assertTrue(statusCode == 403);
			return;
		}

		// retrieve and examine resource
		mvcResult = mockMvc.perform(get(location).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("title").asText().equals("title1"));
		assertTrue(rootNode.get("description").asText().equals("description1"));
		assertTrue(rootNode.get("url").asText().equals("url1"));
		assertTrue(rootNode.get("location").asText().equals("location1"));
		assertTrue(rootNode.get("companyName").asText().equals("companyName1"));
		assertTrue(rootNode.get("openingDate").asText().equals("2024-02-05"));
		assertTrue(rootNode.get("closingDate").asText().equals("2024-02-09"));
		assertTrue(rootNode.get("specialisation").asText().equals("specialisation1"));
		assertTrue(rootNode.get("type").asText().equals("Graduate Job"));
		if (jwtToken.equals(adminJwtToken))
			assertTrue(rootNode.get("isVerified").asBoolean() == true);
		else
			assertTrue(rootNode.get("isVerified").asBoolean() == false);
		assertTrue(rootNode.get("isBookmarked").asBoolean() == false);
		String technology1 = rootNode.get("technologies").get(0).asText();
		String technology2 = rootNode.get("technologies").get(1).asText();
		assertTrue(Arrays.asList(technology1, technology2).contains("foo"));
		assertTrue(Arrays.asList(technology1, technology2).contains("bar"));
	}
	
	@Test
	public void testPostJobWithInvalidInputs() throws Exception {
		testPostJobWithInvalidInputs(adminJwtToken);
		testPostJobWithInvalidInputs(userJwtToken);
		testPostJobWithInvalidInputs(guestJwtToken);
		testPostJobWithInvalidInputs("");
	}

	public void testPostJobWithInvalidInputs(String jwtToken) throws Exception {
		// setup
		jobRepository.deleteAll();
		String responseString, location;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		
		// test wrong date format
		String postBody = "{\n"
				+ "  \"url\": \"url1\",\n"
				+ "  \"openingDate\": \"05/02/2024\"\n"
				+ "}";
		// post a resource
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andReturn();
		int statusCode = mvcResult.getResponse().getStatus();
		if (jwtToken.equals(adminJwtToken) || jwtToken.equals(userJwtToken)) {
			assertTrue(statusCode == 201);
			location = mvcResult.getResponse().getHeader("location");
		} else {
			assertTrue(statusCode == 403);
			return;
		}
		// retrieve and examine resource
		mvcResult = mockMvc.perform(get(location).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("openingDate").isNull());
		assertTrue(rootNode.get("closingDate").isNull());
		
		// test valid and invalid job types
		// valid job type
		jobRepository.deleteAll();
		postBody = "{\n"
				+ "  \"url\": \"url1\",\n"
				+ "  \"type\": \"Internship\"\n"
				+ "}";
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andReturn();
		statusCode = mvcResult.getResponse().getStatus();
		assertTrue(statusCode == 201);
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("type").asText().equals("Internship"));
		
		// invalid job type
		jobRepository.deleteAll();
		postBody = "{\n"
				+ "  \"url\": \"url1\",\n"
				+ "  \"type\": \"internship\"\n"
				+ "}";
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andReturn();
		statusCode = mvcResult.getResponse().getStatus();
		assertTrue(statusCode == 201);
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("type").isNull());
	}
	
	@Test
	@Transactional
	public void testPostWithOverlappingResources() throws Exception {
		String responseString1, responseString2, location1, location2;
		MvcResult mvcResult1, mvcResult2;
		JsonNode rootNode1, rootNode2;
		
		// post a job with admin privilege and then retrieve
		headers.setBearerAuth(adminJwtToken);
		mvcResult1 = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andExpect(status().isCreated()).andReturn();
		location1 = mvcResult1.getResponse().getHeader("location");
		mvcResult1 = mockMvc.perform(get(location1).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString1 = mvcResult1.getResponse().getContentAsString();
		rootNode1 = objectMapper.readTree(responseString1);
		
		// post a job with user privilege and then retrieve
		headers.setBearerAuth(userJwtToken);
		// postBody2 share the same company with postBody1
		// the technologies also overlap
		String postBody2 = "{\n"
				+ "  \"title\": \"title2\",\n"
				+ "  \"description\": \"description2\",\n"
				+ "  \"url\": \"url2\",\n"
				+ "  \"location\": \"location2\",\n"
				+ "  \"companyName\": \"companyName1\",\n"
				+ "  \"openingDate\": \"2024-02-07\",\n"
				+ "  \"closingDate\": \"2024-02-11\",\n"
				+ "  \"specialisation\": \"specialisation1\",\n"
				+ "  \"type\": \"Internship\",\n"
				+ "  \"technologies\": [\n"
				+ "    \"foo\",\n"
				+ "    \"baz\"\n"
				+ "  ]\n"
				+ "}";
		mvcResult2 = mockMvc.perform(post(urlForPost).headers(headers).content(postBody2)).andExpect(status().isCreated()).andReturn();
		location2 = mvcResult2.getResponse().getHeader("location");
		mvcResult2 = mockMvc.perform(get(location2).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString2 = mvcResult2.getResponse().getContentAsString();
		rootNode2 = objectMapper.readTree(responseString2);
		
		// compare overlapping fields

		// check http get response for company properties
		assertTrue(rootNode1.get("companyName").asText().equals("companyName1"));
		assertTrue(rootNode2.get("companyName").asText().equals("companyName1"));
		// check company database records
		Optional<Company> companyOptional = companyRepository.findByCompanyNameIgnoreCase("companyName1");
		assertTrue(companyOptional.isPresent());
		assertTrue(companyRepository.count() == 1);
		Company company = companyOptional.get();
		assertTrue(company.getJobs().size() == 2);
		// check Job database records
		Optional<Job> jobOptional1 = jobRepository.findByUrlIgnoreCase("url1");
		Optional<Job> jobOptional2 = jobRepository.findByUrlIgnoreCase("url2");
		assertTrue(jobOptional1.isPresent());
		assertTrue(jobOptional2.isPresent());
		Job job1 = jobOptional1.get();
		Job job2 = jobOptional2.get();
		assertTrue(job1.getCompany().equals(company));
		assertTrue(job2.getCompany().equals(company));
		
		// check http get response for technology properties
		assertTrue(rootNode1.get("technologies").size() == 2);
		assertTrue(rootNode2.get("technologies").size() == 2);
		Set<String> technologies1 = new HashSet<>();
		rootNode1.get("technologies").forEach(node -> technologies1.add(node.asText()));
		assertTrue(technologies1.containsAll(Arrays.asList("foo", "bar")));
		Set<String> technologies2 = new HashSet<>();
		rootNode2.get("technologies").forEach(node -> technologies2.add(node.asText()));
		assertTrue(technologies2.containsAll(Arrays.asList("foo", "baz")));
		// check technology database records
		Optional<Technology> technologyOptioanl1 = technologyRepository.findByNameIgnoreCase("foo");
		Optional<Technology> technologyOptioanl2 = technologyRepository.findByNameIgnoreCase("bar");
		Optional<Technology> technologyOptioanl3 = technologyRepository.findByNameIgnoreCase("baz");
		assertTrue(technologyOptioanl1.isPresent());
		assertTrue(technologyOptioanl2.isPresent());
		assertTrue(technologyOptioanl3.isPresent());
		Technology technologyFoo = technologyOptioanl1.get();
		Technology technologyBar = technologyOptioanl2.get();
		Technology technologyBaz = technologyOptioanl3.get();
		assertTrue(technologyFoo.getJobs().size() == 2);
		assertTrue(technologyFoo.getJobs().contains(job1));
		assertTrue(technologyFoo.getJobs().contains(job2));
		assertTrue(technologyBar.getJobs().size() == 1);
		assertTrue(technologyBar.getJobs().contains(job1));
		assertTrue(technologyBaz.getJobs().size() == 1);
		assertTrue(technologyBaz.getJobs().contains(job2));
	}
}
