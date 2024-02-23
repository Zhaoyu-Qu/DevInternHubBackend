package com.Jason.DevInternHubBackend;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;
import com.Jason.DevInternHubBackend.domain.Company;
import com.Jason.DevInternHubBackend.domain.CompanyRepository;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.Role;
import com.Jason.DevInternHubBackend.domain.Technology;

@SpringBootApplication
public class DevInternHubBackendApplication implements CommandLineRunner {
	private final CompanyRepository companyRepository;
	private final AppUserRepository appUserRepository;
	private final PasswordEncoder passwordEncoder;
	@Value("${demo.adminUsername}")
	protected String demoAdminUsername;
	@Value("${demo.adminPassword}")
	protected String demoAdminPassword;
	@Value("${demo.userUsername}")
	protected String demoUserUsername;
	@Value("${demo.userPassword}")
	protected String demoUserPassword;
	@Value("${demo.guestUsername}")
	protected String demoGuestUsername;
	@Value("${demo.guestPassword}")
	protected String demoGuestPassword;
	
	

	public DevInternHubBackendApplication(CompanyRepository companyRepository, AppUserRepository appUserRepository,
			PasswordEncoder passwordEncoder) {
		super();
		this.companyRepository = companyRepository;
		this.appUserRepository = appUserRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(DevInternHubBackendApplication.class, args);
	}

	@Override
	// sample data for demonstration purposes only
	public void run(String... args) throws Exception {
		appUserRepository.save(new AppUser(demoAdminUsername, passwordEncoder.encode(demoAdminPassword), "admin"));
		appUserRepository.save(new AppUser(demoUserUsername, passwordEncoder.encode(demoUserPassword), "user"));
		appUserRepository.save(new AppUser(demoGuestUsername, passwordEncoder.encode(demoGuestPassword), "guest"));
	}

}
