package com.Jason.DevInternHubBackend.web;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.Jason.DevInternHubBackend.domain.AccountCredentials;
import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;

@RestController
public class RegistrationController {
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistrationController(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		super();
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody AccountCredentials credentials) {
		Optional<AppUser> appUserOptional = appUserRepository.findByUsernameIgnoreCase(credentials.username());
		if (appUserOptional.isPresent())
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already in use.");
		else if (credentials.username() == null || credentials.username().length() < 6)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username needs to be at least 6 characters.");
		else if (credentials.password() == null || credentials.password().length() < 6)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password needs to be at least 6 characters.");
		appUserRepository
				.save(new AppUser(credentials.username(), passwordEncoder.encode(credentials.password()), "user"));
		return ResponseEntity.ok().build();
	}
}
