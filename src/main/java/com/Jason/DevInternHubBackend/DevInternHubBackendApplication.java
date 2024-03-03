package com.Jason.DevInternHubBackend;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;
import com.Jason.DevInternHubBackend.domain.Company;
import com.Jason.DevInternHubBackend.domain.CompanyRepository;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.JobRepository;
import com.Jason.DevInternHubBackend.domain.Role;
import com.Jason.DevInternHubBackend.domain.Technology;
import com.Jason.DevInternHubBackend.domain.TechnologyRepository;

@SpringBootApplication
public class DevInternHubBackendApplication implements CommandLineRunner {
	private final CompanyRepository companyRepository;
	private final AppUserRepository appUserRepository;
	private final JobRepository jobRepository;
	private final TechnologyRepository technologyRepository;
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
			JobRepository jobRepository, TechnologyRepository technologyRepository, PasswordEncoder passwordEncoder) {
		super();
		this.companyRepository = companyRepository;
		this.appUserRepository = appUserRepository;
		this.jobRepository = jobRepository;
		this.technologyRepository = technologyRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public static void main(String[] args) {
		SpringApplication.run(DevInternHubBackendApplication.class, args);
	}

	@Override
	@Transactional
	// sample data for demonstration purposes only
	public void run(String... args) throws Exception {
		appUserRepository.save(new AppUser(demoAdminUsername, passwordEncoder.encode(demoAdminPassword), "admin"));
		AppUser user = new AppUser(demoUserUsername, passwordEncoder.encode(demoUserPassword), "user");
		appUserRepository.save(user);
		appUserRepository.save(new AppUser(demoGuestUsername, passwordEncoder.encode(demoGuestPassword), "guest"));
		Job job1 = new Job("job1");
		job1.setUrl("url1");
		job1.setClosingDate(LocalDate.parse("2024-04-07"));
		job1.setOpeningDate(LocalDate.parse("2024-07-07"));
		job1.setDescription("demo job");
		job1.setType("Graduate Job");
		job1.setCompany(new Company("company1"));
		job1.getTechnologies().add(new Technology("tech1"));
		job1.getTechnologies().add(new Technology("tech2"));
		job1.setOwner(user);
		user.getOwnedJobs().add(job1);
		technologyRepository.saveAll(job1.getTechnologies());
		companyRepository.save(job1.getCompany());
		jobRepository.save(job1);
	}

}
