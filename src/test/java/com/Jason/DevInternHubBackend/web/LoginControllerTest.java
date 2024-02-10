package com.Jason.DevInternHubBackend.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.Jason.DevInternHubBackend.domain.AppUser;

@SpringBootTest
public class LoginControllerTest extends BaseControllerTest {
	@Test
	public void testAuthentication() throws Exception {
		companyRepository.deleteAll();
		appUserRepository.deleteAll();
		technologyRepository.deleteAll();
		jobRepository.deleteAll();
		appUserRepository.save(
				new AppUser(testUsername, new BCryptPasswordEncoder().encode(testPassword), "admin"));
		mockMvc.perform(post("/login")
				.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}", testUsername, testPassword))
				.header(HttpHeaders.CONTENT_TYPE, "application/json")).andExpect(status().isOk()).andReturn();
	}
}
