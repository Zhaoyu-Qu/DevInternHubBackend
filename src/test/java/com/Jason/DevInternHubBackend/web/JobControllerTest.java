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
	
	String patchBody =
			"{\n"
			+ "  \"title\": \"title3\",\n"
			+ "  \"description\": \"description3\",\n"
			+ "  \"url\": \"url3\",\n"
			+ "  \"location\": \"location3\",\n"
			+ "  \"companyName\": \"companyName3\",\n"
			+ "  \"openingDate\": \"2024-04-07\",\n"
			+ "  \"closingDate\": \"2024-05-17\",\n"
			+ "  \"specialisation\": \"specialisation3\",\n"
			+ "  \"type\": \"Internship\",\n"
			+ "  \"technologies\": [\n"
			+ "    \"foo\",\n"
			+ "    \"baz\"\n"
			+ "  ],\n"
			+ "  \"isVerified\": true\n"
			+ "}"
			;
	
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
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testGetAllJobs(userJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testGetAllJobs(guestJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testGetAllJobs("");
	}

	public void testGetAllJobs(String jwtToken) throws Exception {
		// setup
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		else {
			headers.remove("Authorization");
		}

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
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testGetJob(userJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testGetJob(guestJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testGetJob("");
	}

	public void testGetJob(String jwtToken) throws Exception {
		// setup
		String responseString;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		else {
			headers.remove("Authorization");
		}
		

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
		else {
			headers.remove("Authorization");
		}
		mvcResult = mockMvc.perform(get(urlForGet).headers(headers)).andExpect(status().isOk()).andReturn();
		responseString = mvcResult.getResponse().getContentAsString();
		rootNode = objectMapper.readTree(responseString);
		assertTrue(rootNode.get("title").asText().equals("some job"));
		assertTrue(rootNode.get("url").asText().equals("bar"));
		
		// attempt to get non-existent resource
		String urlForGetNonExistentJob = restBaseApi + "/" + entityNameLowerCasePlural + "/" + 1000;
		mvcResult = mockMvc.perform(get(urlForGetNonExistentJob).headers(headers)).andExpect(status().isNotFound()).andReturn();
	}
	
	@Test
	public void testPostJob() throws Exception {
		testPostJob(adminJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPostJob(userJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPostJob(guestJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPostJob("");
	}
	
	public void testPostJob(String jwtToken) throws Exception {
		// setup
		String responseString, location;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		else {
			headers.remove("Authorization");
		}

		// post a resource
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andReturn();
		int statusCode = mvcResult.getResponse().getStatus();
		if (jwtToken.equals(adminJwtToken) || jwtToken.equals(userJwtToken)) {
			assertTrue(statusCode == 201);
			location = mvcResult.getResponse().getHeader("location");
		} else if (jwtToken.equals(guestJwtToken)){
			assertTrue(statusCode == 403);
			return;
		} else {
			assertTrue(statusCode == 401);
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
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPostJobWithInvalidInputs(userJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPostJobWithInvalidInputs(guestJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPostJobWithInvalidInputs("");
	}

	public void testPostJobWithInvalidInputs(String jwtToken) throws Exception {
		// setup
		String responseString, location;
		MvcResult mvcResult;
		JsonNode rootNode;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		else {
			headers.remove("Authorization");
		}
		
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
		} else if (jwtToken.equals(guestJwtToken)){
			assertTrue(statusCode == 403);
			return;
		} else {
			assertTrue(statusCode == 401);
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
	
	@Test
	public void testPatchJob() throws Exception {
		testPatchJob(adminJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPatchJob(userJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPatchJob(guestJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPatchJob("");
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPatchJobWithInvalidInputs(adminJwtToken);
		super.entityControllerSetUp();
		this.setUpEnvironment();
		testPatchJobWithInvalidInputs(userJwtToken);
	}
	
	@Transactional
	public void testPatchJob(String jwtToken) throws Exception {
		// setup
		String responseStringAdminResource, responseStringUserResource, adminResourceLocation, userResourceLocation;
		MvcResult mvcResult;
		headers.setBearerAuth(adminJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andExpect(status().isCreated()).andReturn();
		adminResourceLocation = mvcResult.getResponse().getHeader("location");
		headers.setBearerAuth(userJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody.replace("1", "2"))).andExpect(status().isCreated()).andReturn();
		userResourceLocation = mvcResult.getResponse().getHeader("location");
		MvcResult mvcResultOnAdminResource, mvcResultOnUserResource;
		JsonNode rootNodeAdminResource, rootNodeUserResource;
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		else {
			headers.remove("Authorization");
		}
		// make patch request
		mvcResultOnAdminResource = mockMvc.perform(patch(adminResourceLocation).headers(headers).content(patchBody)).andReturn();
		mvcResultOnUserResource = mockMvc.perform(patch(userResourceLocation).headers(headers).content(patchBody.replace("3", "4"))).andReturn();
		// examine status code based on authentication
		if (jwtToken.equals(adminJwtToken)) {
			assertTrue(mvcResultOnAdminResource.getResponse().getStatus() == 200);
			assertTrue(mvcResultOnUserResource.getResponse().getStatus() == 200);
		} else if (jwtToken.equals(userJwtToken)) {
			assertTrue(mvcResultOnAdminResource.getResponse().getStatus() == 403);
			assertTrue(mvcResultOnUserResource.getResponse().getStatus() == 200);
		} else if (jwtToken.equals(guestJwtToken)){
			assertTrue(mvcResultOnAdminResource.getResponse().getStatus() == 403);
			assertTrue(mvcResultOnUserResource.getResponse().getStatus() == 403);
			return;
		} else {
			assertTrue(mvcResultOnAdminResource.getResponse().getStatus() == 401);
			assertTrue(mvcResultOnUserResource.getResponse().getStatus() == 401);
			return;
		}
		// examine new values via http get
		mvcResultOnAdminResource = mockMvc.perform(get(adminResourceLocation).headers(headers)).andExpect(status().isOk()).andReturn();
		mvcResultOnUserResource = mockMvc.perform(get(userResourceLocation).headers(headers)).andExpect(status().isOk()).andReturn();
		responseStringAdminResource = mvcResultOnAdminResource.getResponse().getContentAsString();
		responseStringUserResource = mvcResultOnUserResource.getResponse().getContentAsString();
		rootNodeAdminResource = objectMapper.readTree(responseStringAdminResource);
		rootNodeUserResource = objectMapper.readTree(responseStringUserResource);
		
		Long adminResourceId = rootNodeAdminResource.get("id").asLong();
		Long userResourceId = rootNodeUserResource.get("id").asLong();
		Job adminResource = jobRepository.findById(adminResourceId).get();
		Job userResource = jobRepository.findById(userResourceId).get();
		
		// admin can modify any resource
		if (jwtToken.equals(adminJwtToken)) {
			rootNodeAdminResource.get("title").asText().equals("title3");
			rootNodeAdminResource.get("description").asText().equals("description3");
			rootNodeAdminResource.get("url").asText().equals("url3");
			rootNodeAdminResource.get("location").asText().equals("location3");
			rootNodeAdminResource.get("companyName").asText().equals("companyName3");
			rootNodeAdminResource.get("openingDate").asText().equals("2024-04-07");
			rootNodeAdminResource.get("closingDate").asText().equals("2024-05-17");
			rootNodeAdminResource.get("specialisation").asText().equals("specialisation3");
			rootNodeAdminResource.get("type").asText().equals("Internship");
			
			Set<String> technologies1 = new HashSet<>();
			rootNodeAdminResource.get("technologies").forEach(node -> technologies1.add(node.asText()));
			assertTrue(technologies1.containsAll(Arrays.asList("foo", "baz")));
			assertTrue(rootNodeAdminResource.get("isVerified").asBoolean());
			assertFalse(rootNodeAdminResource.get("isBookmarked").asBoolean());
		} else {
			// non-admin users cannot touch others' resources, so everything should stay unchanged.
			rootNodeAdminResource.get("title").asText().equals("title1");
			rootNodeAdminResource.get("description").asText().equals("description1");
			rootNodeAdminResource.get("url").asText().equals("url1");
			rootNodeAdminResource.get("location").asText().equals("location1");
			rootNodeAdminResource.get("companyName").asText().equals("companyName1");
			rootNodeAdminResource.get("openingDate").asText().equals("2024-02-05");
			rootNodeAdminResource.get("closingDate").asText().equals("2024-02-09");
			rootNodeAdminResource.get("specialisation").asText().equals("specialisation1");
			rootNodeAdminResource.get("type").asText().equals("Graduate Job");
			
			Set<String> technologies1 = new HashSet<>();
			rootNodeAdminResource.get("technologies").forEach(node -> technologies1.add(node.asText()));
			assertTrue(technologies1.containsAll(Arrays.asList("foo", "bar")));
		
			assertTrue(rootNodeAdminResource.get("isVerified").asBoolean());
			assertFalse(rootNodeAdminResource.get("isBookmarked").asBoolean());
		}
		
		// the user can modify its own resource, and an admin can modify any resource
		rootNodeUserResource.get("title").asText().equals("title4");
		rootNodeUserResource.get("description").asText().equals("description4");
		rootNodeUserResource.get("url").asText().equals("url4");
		rootNodeUserResource.get("location").asText().equals("location4");
		rootNodeUserResource.get("companyName").asText().equals("companyName3");
		rootNodeUserResource.get("openingDate").asText().equals("2024-04-07");
		rootNodeUserResource.get("closingDate").asText().equals("2024-05-17");
		rootNodeUserResource.get("specialisation").asText().equals("specialisation4");
		rootNodeUserResource.get("type").asText().equals("Internship");
		
		Set<String> technologies1 = new HashSet<>();
		rootNodeUserResource.get("technologies").forEach(node -> technologies1.add(node.asText()));
		assertTrue(technologies1.containsAll(Arrays.asList("foo", "baz")));
	
		if (jwtToken.equals(adminJwtToken))
			assertTrue(rootNodeUserResource.get("isVerified").asBoolean());
		else {
			assertFalse(rootNodeUserResource.get("isVerified").asBoolean());
			assertFalse(rootNodeUserResource.get("isBookmarked").asBoolean());
		}
	}
	
	public void testPatchJobWithInvalidInputs(String jwtToken) throws Exception {
		// setup
		String responseStringAdminResource, responseStringUserResource, adminResourceLocation, userResourceLocation;
		MvcResult mvcResult;
		headers.setBearerAuth(adminJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andExpect(status().isCreated()).andReturn();
		adminResourceLocation = mvcResult.getResponse().getHeader("location");
		headers.setBearerAuth(userJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody.replace("1", "2"))).andExpect(status().isCreated()).andReturn();
		userResourceLocation = mvcResult.getResponse().getHeader("location");
		MvcResult mvcResultOnAdminResource, mvcResultOnUserResource;
		JsonNode rootNodeAdminResource, rootNodeUserResource;
		// try invalid input
		if (jwtToken.length() > 0)
			headers.setBearerAuth(jwtToken);
		else {
			headers.remove("Authorization");
		}
		mvcResultOnUserResource = mockMvc.perform(patch(userResourceLocation).headers(headers).content("{\"type\": \"foo_bar\"}")).andReturn();
		responseStringUserResource = mvcResultOnUserResource.getResponse().getContentAsString();
		rootNodeUserResource = objectMapper.readTree(responseStringUserResource);
		assertTrue(rootNodeUserResource.get("type").asText().equals("Graduate Job"));
	}
	
	@Test
	public void testBookmarkPosting() throws Exception {
		// setup
		String userResourceLocation, adminResourceLocation;
		String adminResourceBookmarkLocation, userResourceBookmarkLocation;
		MvcResult mvcResult;
		headers.setBearerAuth(adminJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andExpect(status().isCreated()).andReturn();
		adminResourceLocation = mvcResult.getResponse().getHeader("location");
		adminResourceBookmarkLocation = adminResourceLocation + "/bookmark";
		
		headers.setBearerAuth(userJwtToken);
		mvcResult = mockMvc.perform(post(urlForPost).headers(headers).content(postBody.replace("1", "2"))).andExpect(status().isCreated()).andReturn();
		userResourceLocation = mvcResult.getResponse().getHeader("location");
		userResourceBookmarkLocation = userResourceLocation + "/bookmark";
		MvcResult mvcResultOnAdminResource, mvcResultOnUserResource;
		headers.setBearerAuth(adminJwtToken);
		// admin make patch request to bookmark resources
		mvcResultOnAdminResource = mockMvc.perform(patch(adminResourceBookmarkLocation).headers(headers).content("{\"isBookmarked\": true}")).andReturn();
		mvcResultOnUserResource = mockMvc.perform(patch(userResourceBookmarkLocation).headers(headers).content("{\"isBookmarked\": true}")).andReturn();
		mvcResultOnAdminResource = mockMvc.perform(get(adminResourceLocation).headers(headers)).andReturn();
		mvcResultOnUserResource = mockMvc.perform(get(userResourceLocation).headers(headers)).andReturn();
		// to the admin, the resources are bookmarked
		assertTrue(objectMapper.readTree(mvcResultOnAdminResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		assertTrue(objectMapper.readTree(mvcResultOnUserResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		// to other users, the resources remain unbookmarked
		headers.setBearerAuth(userJwtToken);
		mvcResultOnAdminResource = mockMvc.perform(get(adminResourceLocation).headers(headers)).andReturn();
		mvcResultOnUserResource = mockMvc.perform(get(userResourceLocation).headers(headers)).andReturn();
		assertFalse(objectMapper.readTree(mvcResultOnAdminResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		assertFalse(objectMapper.readTree(mvcResultOnUserResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		
		// admin unbookmarks user resource, user bookmarks admin resource
		headers.setBearerAuth(adminJwtToken);
		mvcResultOnUserResource = mockMvc.perform(patch(userResourceBookmarkLocation).headers(headers).content("{\"isBookmarked\": false}")).andReturn();
		headers.setBearerAuth(userJwtToken);
		mvcResultOnAdminResource = mockMvc.perform(patch(adminResourceBookmarkLocation).headers(headers).content("{\"isBookmarked\": true}")).andReturn();
		
		// retrieve resource as admin
		headers.setBearerAuth(adminJwtToken);
		mvcResultOnAdminResource = mockMvc.perform(get(adminResourceLocation).headers(headers)).andReturn();
		mvcResultOnUserResource = mockMvc.perform(get(userResourceLocation).headers(headers)).andReturn();
		assertTrue(objectMapper.readTree(mvcResultOnAdminResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		assertFalse(objectMapper.readTree(mvcResultOnUserResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		
		// retrieve resource as user
		headers.setBearerAuth(userJwtToken);
		mvcResultOnAdminResource = mockMvc.perform(get(adminResourceLocation).headers(headers)).andReturn();
		mvcResultOnUserResource = mockMvc.perform(get(userResourceLocation).headers(headers)).andReturn();
		assertTrue(objectMapper.readTree(mvcResultOnAdminResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
		assertFalse(objectMapper.readTree(mvcResultOnUserResource.getResponse().getContentAsString()).get("isBookmarked").asBoolean());
	}
	
}
