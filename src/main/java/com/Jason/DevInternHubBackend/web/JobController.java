package com.Jason.DevInternHubBackend.web;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;
import com.Jason.DevInternHubBackend.domain.Company;
import com.Jason.DevInternHubBackend.domain.CompanyRepository;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.JobCreationDto;
import com.Jason.DevInternHubBackend.domain.JobResponseDto;
import com.Jason.DevInternHubBackend.domain.JobUpdateDto;
import com.Jason.DevInternHubBackend.domain.JobRepository;
import com.Jason.DevInternHubBackend.domain.Technology;
import com.Jason.DevInternHubBackend.domain.TechnologyRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("${entityController.basePath}")
public class JobController {
	private final JobRepository jobRepository;
	private final AppUserRepository appUserRepository;
	private final CompanyRepository companyRepository;
	private final TechnologyRepository technologyRepository;

	public JobController(JobRepository jobRepository, AppUserRepository appUserRepository,
			CompanyRepository companyRepository, TechnologyRepository technologyRepository) {
		super();
		this.jobRepository = jobRepository;
		this.appUserRepository = appUserRepository;
		this.companyRepository = companyRepository;
		this.technologyRepository = technologyRepository;
	}

	@GetMapping("/jobs")
	public Iterable<JobResponseDto> getAllJobs() {
		Set<JobResponseDto> jobDtos = new HashSet<>();
		for(Job job : jobRepository.findAll()) {
			jobDtos.add(convertToJobResponseDto(job));
		}
		return jobDtos;
	}

	@GetMapping("/jobs/{id}")
	public ResponseEntity<JobResponseDto> getJob(@PathVariable("id") Long id) {
		Optional<Job> jobOptional = jobRepository.findById(id);
		ResponseEntity<JobResponseDto> response;
		if (jobOptional.isPresent()) {
			JobResponseDto jobOutDto = convertToJobResponseDto(jobOptional.get());
			response = new ResponseEntity<>(jobOutDto, HttpStatus.OK);
		} else {
		response = ResponseEntity.notFound().build();
		}
		return response;
	}

	@PostMapping(path = "/jobs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public ResponseEntity<JobResponseDto> postJob(@RequestBody JobCreationDto jobDto) {
		Job job = convertToJob(jobDto);
		
		// Whoever creates this resource is the owner
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getPrincipal().toString();
		AppUser user = appUserRepository.findByUsernameIgnoreCase(username).get();
		job.setOwner(user);
		
		// resources added by an admin are verified
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			job.setVerified(true);
		} else {
			job.setVerified(false);
		}
		jobRepository.save(job);
		appUserRepository.save(user);
		companyRepository.save(job.getCompany());
		technologyRepository.saveAll(job.getTechnologies());

		return new ResponseEntity<>(convertToJobResponseDto(job), HttpStatus.CREATED);
	}
	
	@PatchMapping(path = "/jobs/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public ResponseEntity<JobResponseDto> patchJob(@PathVariable("id") Long id, @RequestBody JobUpdateDto jobDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getPrincipal().toString();
		AppUser user = appUserRepository.findByUsernameIgnoreCase(username).get();
		
		Optional<Job> jobOptional = jobRepository.findById(id);
		if (jobOptional.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		Job job = jobOptional.get();
		
		// non-admin users may only update resources that belong to them
		if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !user.equals(job.getOwner())) {
			return ResponseEntity.status(403).build();
		}
		
		// the following code update database record according to the patch request
		if (jobDto.getIsBookmarked() == true) {
			job.addBookmarkHolder(user);
			user.addBookmarkedjob(job);
		}
		else if (jobDto.getIsBookmarked() == false) {
			job.getBookmarkHolders().remove(user);
			user.getBookmarkedJobs().remove(job);
		}
		if (jobDto.getIsVerified() != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
			job.setVerified(jobDto.getIsVerified());
		try {
			job.setClosingDate(LocalDate.parse(jobDto.getClosingDate()));
		} catch (DateTimeParseException e) {}
		try {
			job.setOpeningDate(LocalDate.parse(jobDto.getOpeningDate()));
		} catch (DateTimeParseException e) {}
		if (jobDto.getCompanyName() != null && !job.getCompanyName().equals(jobDto.getCompanyName())) {
			companyRepository.save(job.getCompany());
			job.getCompany().getJobs().remove(job);
			job.setCompany(new Company(jobDto.getCompanyName()));
		}
		if (jobDto.getDescription() != null)
			job.setDescription(jobDto.getDescription());
		if (jobDto.getLocation() != null)
			job.setLocation(jobDto.getLocation());
		if (jobDto.getSpecialisation() != null)
			job.setSpecialisation(jobDto.getSpecialisation());
		if (!jobDto.getTechnologies().isEmpty()) {
			// loop through current technology set associated with the job
			for (Technology t : job.getTechnologies()) {
				// if a technology is not specified in the updateDto, then remove
				if (!jobDto.getTechnologies().contains(t.getName())) {
					t.getJobs().remove(job);
					job.getTechnologies().remove(t);
					technologyRepository.save(t);
				} else {
					jobDto.getTechnologies().remove(t.getName());
				}
			}
			for (String t : jobDto.getTechnologies()) {
				Optional<Technology> technologyOptional = technologyRepository.findByNameIgnoreCase(t);
				if (technologyOptional.isPresent()) {
					Technology existingTechnology = technologyOptional.get();
					existingTechnology.addJob(job);
					technologyRepository.save(existingTechnology);
					job.addTechnology(existingTechnology);
				} else {
					Technology newTechnology = new Technology(t);
					job.addTechnology(newTechnology);
					technologyRepository.save(newTechnology);
				}
			}
		}
		if (jobDto.getTitle() != null)
			job.setTitle(jobDto.getTitle());
		if (jobDto.getType() != null)
			job.setType(jobDto.getType());
		if (jobDto.getUrl() != null)
			job.setUrl(jobDto.getUrl());
		jobRepository.save(job);
		appUserRepository.save(user);
		return new ResponseEntity<>(convertToJobResponseDto(job), HttpStatus.OK);
	}
	
	private JobResponseDto convertToJobResponseDto(Job job) {
		// get requesting user's database record
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getPrincipal().toString();
		Optional<AppUser> appUserOptional = appUserRepository.findByUsernameIgnoreCase(username);
		AppUser user = null;
		if (appUserOptional.isPresent())
			user = appUserOptional.get();

		JobResponseDto j = new JobResponseDto();
		j.setId(job.getId());
		if (job.getClosingDate() != null)
			j.setClosingDate(job.getClosingDate().toString());
		j.setCompanyName(j.getCompanyName());
		if (job.getCompany() != null)
			j.setCompanyUrl(job.getCompany().getUrl());
		j.setDescription(job.getDescription());
		j.setIsBookmarked(job.getBookmarkHolders().contains(user));
		j.setIsVerified(job.isVerified());
		j.setLocation(job.getLocation());
		if (job.getOpeningDate() != null)
			j.setOpeningDate(job.getOpeningDate().toString());
		j.setSpecialisation(job.getSpecialisation());
		for (Technology t : job.getTechnologies()) {
			j.getTechnologies().add(t.getName());
		}
		j.setTitle(job.getTitle());
		j.setUrl(job.getUrl());
		return j;
	}
	
	private Job convertToJob(JobCreationDto j) {
		Job job = new Job();
		try {
			LocalDate openingDate = LocalDate.parse(j.getOpeningDate());
			job.setOpeningDate(openingDate);
		} catch (DateTimeParseException e) {
			job.setOpeningDate(null);
		}

		try {
			LocalDate closingDate = LocalDate.parse(j.getClosingDate());
			job.setClosingDate(closingDate);
		} catch (DateTimeParseException e) {
			job.setClosingDate(null);
		}
		
		String companyName = j.getCompanyName();
		Optional<Company> companyOptional = companyRepository.findByCompanyNameIgnoreCase(companyName);
		if (companyOptional.isPresent())
			job.setCompany(companyOptional.get());
		
		job.setDescription(j.getDescription());
		job.setLocation(j.getLocation());
		job.setSpecialisation(j.getSpecialisation());
		
		for (String t : j.getTechnologies()) {
			Optional<Technology> technologyOptional = technologyRepository.findByNameIgnoreCase(t);
			if (technologyOptional.isPresent())
				job.addTechnology(technologyOptional.get());
			else
				job.addTechnology(new Technology(t));
		}
		
		job.setTitle(j.getTitle());
		job.setType(j.getType());
		job.setUrl(j.getUrl());
		
		return job;
	}
}
