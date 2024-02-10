package com.Jason.DevInternHubBackend.domain;

import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Company implements DatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String companyName, url;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "company")
	private List<Job> jobs = new ArrayList<Job>();

	public Company() {
	}

	public Company(String companyName) {
		this.companyName = companyName;
	}

	public Company(String companyName, String url) {
		this.companyName = companyName;
		this.url = url;
	}

	public Long getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void addJob(Job job) {
		if (!this.jobs.contains(job)) {
			this.jobs.add(job);
			job.setCompany(this);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Company) {
			Company company = (Company) o;
			return Objects.equals(this.getCompanyName(), company.getCompanyName())
					&& Objects.equals(this.getUrl(), company.getUrl())
					&& Objects.equals(this.getJobs(), company.getJobs());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("ID: %d|Name: %s|Job Count: %d", this.getId(), this.getCompanyName(), this.getJobs().size());
	}
	
	@Override
	public DatabaseEntity generateFields() {
		this.setCompanyName(DatabaseEntity.generateRandomString());
		this.setUrl(DatabaseEntity.generateRandomString());
		return this;
	}

}
