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
		Technology tech1, tech2;
		tech1 = new Technology("Java");
		tech2 = new Technology("Python");

		Company company1, company2;
		company1 = new Company("Google");
		company1.setUrl("www.google.com");
		company2 = new Company("Microsoft");
		company2.setUrl("www.microsoft.com");

		LocalDate dateNow = LocalDate.now();
		Job job1, job2, job3;
		job1 = new Job("Backend Developer", company1);
		job1.setDescription("I bet it's good!");
		job1.setOpeningDate(dateNow);
		job1.setClosingDate(dateNow.plusDays(30));
		job1.setLocation("Australia");
		job1.setSpecialisation("Backend");
		job1.setType("Graduate Job");
		job1.setUrl("seek.com.au");
		job1.addTechnology(tech1);

		job2 = new Job("Frontend Developer", company1);
		job2.setDescription("Probably boring");
		job2.setOpeningDate(dateNow.minusDays(10));
		job2.setClosingDate(dateNow.plusDays(15));
		job2.setLocation("Australia");
		job2.setSpecialisation("Frontend");
		job2.setType("Internship");
		job2.setUrl("seek.com.au");
		job2.addTechnology(tech2);

		job3 = new Job("Data Scientist", company2);
		job3.setDescription("The heck is this?!");
		job3.setOpeningDate(dateNow.minusDays(11));
		job3.setClosingDate(dateNow.plusDays(6));
		job3.setLocation("New Zealand");
		job3.setSpecialisation("Data Science");
		job3.setType("Internship");
		job3.setUrl("seek.co.nz");
		job3.addTechnology(tech2);

		company1.addJob(job1);
		company1.addJob(job2);
		company2.addJob(job3);

		companyRepository.saveAll(Arrays.asList(company1, company2));
		appUserRepository.save(new AppUser(demoAdminUsername, passwordEncoder.encode(demoAdminPassword), Role.ADMIN));
		appUserRepository.save(new AppUser(demoUserUsername, passwordEncoder.encode(demoUserPassword), Role.USER));
		appUserRepository.save(new AppUser(demoGuestUsername, passwordEncoder.encode(demoGuestPassword), Role.GUEST));
	}

}
