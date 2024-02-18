package com.Jason.DevInternHubBackend.web;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Jason.DevInternHubBackend.domain.Job;
import com.Jason.DevInternHubBackend.domain.JobRepository;

@RestController
@RequestMapping("${entityController.basePath}")
public class JobController {
	private final JobRepository jobRepository;

	public JobController(JobRepository jobRepository) {
		super();
		this.jobRepository = jobRepository;
	}
	
	@GetMapping("/jobs")
	public Iterable<Job> getAllJobs() {
		return jobRepository.findAll();
	}
	
	@GetMapping("/jobs/{id}")
	public Optional<Job> getJob(@PathVariable("id") Long id) {
		return jobRepository.findById(id);
	}
}
