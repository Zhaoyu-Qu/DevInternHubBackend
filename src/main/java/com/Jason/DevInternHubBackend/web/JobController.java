package com.Jason.DevInternHubBackend.web;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.Objects;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
	@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Job returned successfully", 
		                 content = @Content(mediaType = "application/json", 
		                 schema = @Schema(implementation = JobResponseDto.class))),
		    @ApiResponse(responseCode = "404", description = "Job not found", 
		                 content = @Content(mediaType = "application/json"))
		})
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
	@ApiResponses(value = {
		    @ApiResponse(responseCode = "201", description = "Job created successfully", 
		                 content = @Content(mediaType = "application/json", 
		                 schema = @Schema(implementation = JobResponseDto.class))),
		    @ApiResponse(responseCode = "400", description = "Bad Request, failed to create posting due to missing URL", 
		                 content = @Content(mediaType = "application/json")),
		    @ApiResponse(responseCode = "409", description = "Conflict, the URL already exists", 
		                 content = @Content(mediaType = "application/json"))
		})
	public ResponseEntity<?> postJob(@RequestBody JobCreationDto jobDto) {
		// return error code if url is not provided
		if (jobDto.getUrl() == null)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create posting. You must provide the original url of the posting!");
		// return error code if url is not unique
		if (jobRepository.findByUrlIgnoreCase(jobDto.getUrl()).isPresent())
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to create posting. The url already exists.");
		
		Job job = convertToJob(jobDto);
		
		// set job owner to whoever creates this resource
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getPrincipal().toString();
		AppUser user = appUserRepository.findByUsernameIgnoreCase(username).get();
		job.setOwner(user);
		user.getOwnedJobs().add(job);
		
		// set isVerified status based on the creator's role
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			job.setIsVerified(true);
		} else {
			job.setIsVerified(false);
		}
		
		jobRepository.save(job);
		appUserRepository.save(user);
		if (job.getCompany() != null)
			companyRepository.save(job.getCompany());
		if (!job.getTechnologies().isEmpty())
			technologyRepository.saveAll(job.getTechnologies());
		
		//include a location property in the response header
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
			    .path("/{id}")
			    .buildAndExpand(job.getId())
			    .toUri();
		return ResponseEntity.created(location).body(convertToJobResponseDto(job));
	}
	
	@PatchMapping(path = "/jobs/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	@Secured({ "ROLE_ADMIN", "ROLE_USER" })
	@ApiResponses(value = {
		    @ApiResponse(responseCode = "200", description = "Job updated successfully", 
		                 content = @Content(mediaType = "application/json", 
		                 schema = @Schema(implementation = JobResponseDto.class))),
		    @ApiResponse(responseCode = "409", description = "Conflict, the URL already exists", 
		                 content = @Content(mediaType = "application/json")),
		    @ApiResponse(responseCode = "403", description = "non-admin user cannot update others' resources", 
            content = @Content(mediaType = "application/json")),
		    @ApiResponse(responseCode = "404", description = "resource not found", 
            content = @Content(mediaType = "application/json"))
		})
	public ResponseEntity<?> patchJob(@PathVariable("id") Long id, @RequestBody JobUpdateDto jobDto) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getPrincipal().toString();
		AppUser user = appUserRepository.findByUsernameIgnoreCase(username).get();
		
		Optional<Job> jobOptional = jobRepository.findById(id);
		if (jobOptional.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to update posting. Resource not found.");
		}
		Job job = jobOptional.get();
		
		// return error code if non-admin users attempt to update resources that do not belong to them
		if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) && !user.equals(job.getOwner())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Failed to update posting. Non-admin users only have access to their own resources.");
		}
		
		// return error code if url is not unique
		if (jobDto.getUrl() != null && !jobDto.getUrl().equals(job.getUrl()) && jobRepository.findByUrlIgnoreCase(jobDto.getUrl()).isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Failed to update posting. The url is found in another posting.");
		}
		
		// the following code update database record according to the patch request
		technologyRepository.saveAll(job.getTechnologies());
		companyRepository.save(job.getCompany());
		appUserRepository.save(job.getOwner());
		appUserRepository.saveAll(job.getBookmarkHolders());
		jobRepository.save(job);
		
		if (jobDto.getIsBookmarked() != null && jobDto.getIsBookmarked() == true) {
			job.getBookmarkHolders().add(user);
			user.getBookmarkedJobs().add(job);
		} else if (jobDto.getIsBookmarked() != null && jobDto.getIsBookmarked() == false) {
			job.getBookmarkHolders().remove(user);
			user.getBookmarkedJobs().remove(job);
		}
		
		if (jobDto.getIsVerified() != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			job.setIsVerified(jobDto.getIsVerified());
		}
		
		if (jobDto.getOpeningDate() != null) {
			try {
				LocalDate openingDate = LocalDate.parse(jobDto.getOpeningDate());
				job.setOpeningDate(openingDate);
			} catch (Exception e) { }
		}
		
		if (jobDto.getClosingDate() != null) {
			try {
				LocalDate closingDate = LocalDate.parse(jobDto.getClosingDate());
				job.setClosingDate(closingDate);
			} catch (Exception e) { }
		}
		if (jobDto.getCompanyName() != null) {
			// unhook current company
			try {
				job.getCompany().getJobs().remove(job);
				job.setCompany(null);
			} catch (Exception e) {}
			
			// link new company
			String newCompanyName = jobDto.getCompanyName();
			Optional<Company> companyOptional = companyRepository.findByCompanyNameIgnoreCase(newCompanyName);
			if (companyOptional.isEmpty()) {
				Company newCompany = new Company(jobDto.getCompanyName());
				companyRepository.save(newCompany);
				job.setCompany(newCompany);
				job.getCompany().getJobs().add(job);
			} else {
				companyOptional.get().getJobs().add(job);
				job.setCompany(companyOptional.get());
			}
		}
		if (jobDto.getTechnologies().size() > 0) {
			// clear the job's current technology records
			for (Technology t : job.getTechnologies()) {
				t.getJobs().remove(job);
			}
			job.getTechnologies().clear();
			
			for (String newTechName : jobDto.getTechnologies()) {
				Technology newTechnology;
				Optional<Technology> technologyOptional = technologyRepository.findByNameIgnoreCase(newTechName);
				if (technologyOptional.isPresent()) {
					newTechnology = technologyOptional.get();
				} else {
					newTechnology = new Technology(newTechName);
				}
				job.getTechnologies().add(newTechnology);
				newTechnology.getJobs().add(job);
			}
		}
		
		if (jobDto.getDescription() != null)
			job.setDescription(jobDto.getDescription());
		if (jobDto.getLocation() != null)
			job.setLocation(jobDto.getLocation());
		if (jobDto.getSpecialisation() != null)
			job.setSpecialisation(jobDto.getSpecialisation());
		

		
		if (jobDto.getTitle() != null)
			job.setTitle(jobDto.getTitle());
		if (jobDto.getType() != null)
			job.setType(jobDto.getType());
		if (jobDto.getUrl() != null) {
			job.setUrl(jobDto.getUrl());
		}
		
		technologyRepository.saveAll(job.getTechnologies());
		companyRepository.save(job.getCompany());
		appUserRepository.save(job.getOwner());
		appUserRepository.saveAll(job.getBookmarkHolders());
		jobRepository.save(job);
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
		if (job.getCompany() != null) {
			j.setCompanyName(job.getCompany().getCompanyName());
			j.setCompanyUrl(job.getCompany().getUrl());
		}
		j.setDescription(job.getDescription());
		j.setIsBookmarked(job.getBookmarkHolders().contains(user));
		j.setIsVerified(job.getIsVerified());
		j.setLocation(job.getLocation());
		if (job.getOpeningDate() != null)
			j.setOpeningDate(job.getOpeningDate().toString());
		if (job.getClosingDate() != null)
			j.setClosingDate(job.getClosingDate().toString());
		j.setSpecialisation(job.getSpecialisation());
		for (Technology t : job.getTechnologies()) {
			j.getTechnologies().add(t.getName());
		}
		j.setTitle(job.getTitle());
		j.setUrl(job.getUrl());
		j.setType(job.getType());
		return j;
	}
	
	private Job convertToJob(JobCreationDto j) {
		Job job = new Job();
		try {
			LocalDate openingDate = LocalDate.parse(j.getOpeningDate());
			job.setOpeningDate(openingDate);
		} catch (Exception e) {
			job.setOpeningDate(null);
		}

		try {
			LocalDate closingDate = LocalDate.parse(j.getClosingDate());
			job.setClosingDate(closingDate);
		} catch (Exception e) {
			job.setClosingDate(null);
		}
		
		String companyName = j.getCompanyName();
		if (companyName != null) {
			Optional<Company> companyOptional = companyRepository.findByCompanyNameIgnoreCase(companyName);
			Company company = null;
			if (companyOptional.isPresent())
				company = companyOptional.get();
			else
				company = new Company(companyName);
			job.setCompany(company);
			company.getJobs().add(job);
		}
		
		job.setDescription(j.getDescription());
		job.setLocation(j.getLocation());
		job.setSpecialisation(j.getSpecialisation());
		
		for (String t : j.getTechnologies()) {
			Optional<Technology> technologyOptional = technologyRepository.findByNameIgnoreCase(t);
			if (technologyOptional.isPresent()) {
				job.getTechnologies().add(technologyOptional.get());
				technologyOptional.get().getJobs().add(job);
			}
			else {
				Technology newTechnology = new Technology(t);
				job.getTechnologies().add(newTechnology);
				newTechnology.getJobs().add(job);
			}
		}
		
		job.setTitle(j.getTitle());
		job.setType(j.getType());
		job.setUrl(j.getUrl());
		
		return job;
	}
}
