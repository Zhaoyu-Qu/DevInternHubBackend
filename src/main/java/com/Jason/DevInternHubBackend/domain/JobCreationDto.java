package com.Jason.DevInternHubBackend.domain;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class JobCreationDto extends JobBaseDto {

//	public static JobCreationDto convertToJobDto(Job job) {
//		JobCreationDto jobDto = new JobCreationDto();
//		jobDto.setVerified(job.isVerified());
//		jobDto.setUrl(job.getUrl());
//		jobDto.setType(job.getType());
//		jobDto.setTitle(job.getTitle());
//		jobDto.setTechnologies(job.getTechnologies());
//		jobDto.setSpecialisation(job.getSpecialisation());
//		if (job.getOpeningDate() != null)
//			jobDto.setOpeningDate(job.getOpeningDate().toString());
//		jobDto.setLocation(job.getLocation());
//		jobDto.setDescription(job.getDescription());
//		jobDto.setCompanyName(job.getCompanyName());
//		jobDto.setId(job.getId());
//		if (job.getClosingDate() != null)
//			jobDto.setClosingDate(job.getClosingDate().toString());
//		return jobDto;
//	}
//
//	public static Job convertToJob(JobCreationDto jobDto) {
//		Job job = new Job();
//		job.setTitle(jobDto.getTitle());
//		job.setCompanyName(jobDto.getCompanyName());
//		job.setLocation(jobDto.getLocation());
//		try {
//			LocalDate openingDate = LocalDate.parse(jobDto.getOpeningDate());
//			job.setOpeningDate(openingDate);
//		} catch (DateTimeParseException e) {
//			job.setOpeningDate(null);
//		}
//
//		try {
//			LocalDate closingDate = LocalDate.parse(jobDto.getClosingDate());
//			job.setClosingDate(closingDate);
//		} catch (DateTimeParseException e) {
//			job.setClosingDate(null);
//		}
//
//		job.setDescription(jobDto.getDescription());
//		job.setLocation(jobDto.getLocation());
//		job.setSpecialisation(jobDto.getSpecialisation());
//		job.setType(jobDto.getType());
//		job.setUrl(jobDto.getUrl());
//		return job;
//	}
}
