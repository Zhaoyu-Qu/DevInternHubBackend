package com.Jason.DevInternHubBackend.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;

@SpringBootTest
public class RegistrationControllerTest extends BaseControllerTest {
	HttpHeaders headers;

	@BeforeEach
	public void setUpEnvironment() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		jobRepository.deleteAll();
		appUserRepository.deleteAll();
	}

	@Test
	public void testRegistration() throws Exception {
		MvcResult mvcResult;

		// register
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"username\": \"abcdefg\",\n" + "  \"password\": \"1234567\"\n" + "}"))
				.andExpect(status().isOk());
		// examine database record
		assertTrue(appUserRepository.count() == 1);
		assertTrue(appUserRepository.findByUsernameIgnoreCase("abcdefg").isPresent());

		// register with invalid input
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"username\": \"abcdefg\",\n" + "  \"password\": \"1234567\"\n" + "}"))
				.andExpect(status().isConflict());
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"username\": \"abcd\",\n" + "  \"password\": \"1234567\"\n" + "}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"username\": \"abcd\",\n" + "  \"password\": \"1567\"\n" + "}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"username\": \"abcdefg\"}"))
				.andExpect(status().isConflict());
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"username\": \"abcdefgh\"}"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(post("/register").headers(headers)
				.content("{\n" + "  \"password\": \"1234567\"}"))
				.andExpect(status().isBadRequest());
		
		// log in with username and password that have just been registered
		mvcResult = mockMvc.perform(post("/login").headers(headers)
				.content("{\n" + "  \"username\": \"abcdefg\",\n" + "  \"password\": \"1234567\"\n" + "}"))
				.andExpect(status().isOk()).andReturn();
		String jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		assertTrue(jwtToken != null && jwtToken.startsWith("Bearer"));
		
		
		// post resources using the retrieved JWT token
		headers.setBearerAuth(jwtToken);
		String urlForPost = restBaseApi + "/" + "jobs";
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
		mockMvc.perform(post(urlForPost).headers(headers).content(postBody)).andExpect(status().isCreated());
	}

}
