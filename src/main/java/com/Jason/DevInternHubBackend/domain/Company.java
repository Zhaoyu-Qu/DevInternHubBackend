package com.Jason.DevInternHubBackend.domain;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
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
	@Column(nullable = false, updatable = false, unique = true)
	private Long id;
	@Column(nullable = false, unique = true)
	private String companyName;
	@Column(unique = true)
	private String url;
	@JsonIgnore
	@OneToMany(mappedBy = "company")
	private Set<Job> jobs = new HashSet<>();

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

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Set<Job> getJobs() {
		return jobs;
	}

	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Company) {
			Company company = (Company) o;
			return Objects.equals(this.getCompanyName(), company.getCompanyName())
					&& Objects.equals(this.getUrl(), company.getUrl());
		}
		return false;
	}
	
	

	@Override
	public int hashCode() {
		return Objects.hash(companyName, url);
	}

	@Override
	public DatabaseEntity generateFields() {
		this.setCompanyName(DatabaseEntity.generateRandomString());
		this.setUrl(DatabaseEntity.generateRandomString());
		return this;
	}

}
