package com.Jason.DevInternHubBackend.domain;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class JobDto {
	private String title, description, url, location, companyName;
	private String openingDate, closingDate;
	private String specialisation; // backend, frontend, mobile development, etc.
	private String type; // graduate jobs, internships, entry level jobs, etc.
	private boolean isVerified;
	private Set<Technology> technologies;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}

	public String getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(String closingDate) {
		this.closingDate = closingDate;
	}

	public String getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Set<Technology> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(Set<Technology> technologies) {
		this.technologies = technologies;
	}

	public static JobDto convertToJobDto(Job job) {
		JobDto jobDto = new JobDto();
		jobDto.setVerified(job.isVerified());
		jobDto.setUrl(job.getUrl());
		jobDto.setType(job.getType());
		jobDto.setTitle(job.getTitle());
		jobDto.setTechnologies(job.getTechnologies());
		jobDto.setSpecialisation(job.getSpecialisation());
		if (job.getOpeningDate() != null)
			jobDto.setOpeningDate(job.getOpeningDate().toString());
		jobDto.setLocation(job.getLocation());
		jobDto.setDescription(job.getDescription());
		jobDto.setCompanyName(job.getCompanyName());
		if (job.getClosingDate() != null)
			jobDto.setClosingDate(job.getClosingDate().toString());
		return jobDto;
	}
	
	public static Job convertToJob(JobDto jobDto) {
		Job job = new Job();
		job.setTitle(jobDto.getTitle());
		job.setCompanyName(jobDto.getCompanyName());
		job.setLocation(jobDto.getLocation());
		try {
			LocalDate openingDate = LocalDate.parse(jobDto.getOpeningDate());
			job.setOpeningDate(openingDate);
		} catch (DateTimeParseException e) {
			job.setOpeningDate(null);
		}

		try {
			LocalDate closingDate = LocalDate.parse(jobDto.getClosingDate());
			job.setClosingDate(closingDate);
		} catch (DateTimeParseException e) {
			job.setClosingDate(null);
		}

		job.setDescription(jobDto.getDescription());
		job.setLocation(jobDto.getLocation());
		job.setSpecialisation(jobDto.getSpecialisation());
		job.setType(jobDto.getType());
		job.setUrl(jobDto.getUrl());
		return job;
	}
}
