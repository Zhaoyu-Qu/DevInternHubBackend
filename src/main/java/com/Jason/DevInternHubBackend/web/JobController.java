package com.Jason.DevInternHubBackend.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Jason.DevInternHubBackend.domain.AppUser;
import com.Jason.DevInternHubBackend.domain.AppUserRepository;
import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.JobDto;
import com.Jason.DevInternHubBackend.domain.JobRepository;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("${entityController.basePath}")
public class JobController {
	private final JobRepository jobRepository;
	private final AppUserRepository appUserRepository;

	public JobController(JobRepository jobRepository, AppUserRepository appUserRepository) {
		super();
		this.jobRepository = jobRepository;
		this.appUserRepository = appUserRepository;
	}

	@GetMapping("/jobs")
	public Iterable<JobDto> getAllJobs() {
		Set<JobDto> jobDtos = new HashSet<>();
		for(Job job : jobRepository.findAll()) {
			jobDtos.add(JobDto.convertToJobDto(job));
		}
		return jobDtos;
	}

	@GetMapping("/jobs/{id}")
	public ResponseEntity<JobDto> getJob(@PathVariable("id") Long id) {
		Optional<Job> job = jobRepository.findById(id);
		ResponseEntity<JobDto> response;
		if (job.isPresent()) {
			response = new ResponseEntity<>(JobDto.convertToJobDto(job.get()), HttpStatus.OK);
		} else {
		response = ResponseEntity.notFound().build();
		}
		return response;
	}

	@PostMapping(path = "/jobs", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	public ResponseEntity<JobDto> postJob(@RequestBody JobDto jobDto) {
		Job job = JobDto.convertToJob(jobDto);
		
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

		return new ResponseEntity<>(JobDto.convertToJobDto(job), HttpStatus.CREATED);
	}
}
