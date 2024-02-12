package com.Jason.DevInternHubBackend.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;

import com.Jason.DevInternHubBackend.domain.AppUser;

@SpringBootTest
public class LoginControllerTest extends BaseControllerTest {
	@Test
	public void testAuthentication() throws Exception {
		appUserRepository.deleteAll();
		appUserRepository
				.save(new AppUser(demoAdminUsername, new BCryptPasswordEncoder().encode(demoAdminPassword), "admin"));
		appUserRepository
				.save(new AppUser(demoUserUsername, new BCryptPasswordEncoder().encode(demoUserPassword), "user"));
		appUserRepository
				.save(new AppUser(demoGuestUsername, new BCryptPasswordEncoder().encode(demoGuestPassword), "guest"));
		// test getting an admin JWT token
		MvcResult mvcResult = mockMvc
				.perform(
						post("/login")
								.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}",
										demoAdminUsername, demoAdminPassword))
								.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		String jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		assertTrue(jwtToken != null && jwtToken.startsWith("Bearer"));

		// test getting an user JWT token
		mvcResult = mockMvc
				.perform(
						post("/login")
								.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}",
										demoUserUsername, demoUserPassword))
								.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		assertTrue(jwtToken != null && jwtToken.startsWith("Bearer"));
		// test getting an guest JWT token
		mvcResult = mockMvc
				.perform(
						post("/login")
								.content(String.format("{\"username\":\"%s\",\"password\"" + ":\"%s\"}",
										demoGuestUsername, demoGuestPassword))
								.header(HttpHeaders.CONTENT_TYPE, "application/json"))
				.andExpect(status().isOk()).andReturn();
		jwtToken = mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
		assertTrue(jwtToken != null && jwtToken.startsWith("Bearer"));
		assertTrue(jwtToken.length() > 15);
	}
}
