package com.Jason.DevInternHubBackend.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Technology implements DatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String name;
	@JsonIgnore
	@ManyToMany(mappedBy = "technologies")
	private Set<Job> jobs = new HashSet<Job>();

	public Technology() {
	}

	public Technology(String technology) {
		this.name = technology;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Job> getJobs() {
		return jobs;
	}

	public void addJob(Job job) {
		if (!jobs.contains(job)) {
			this.jobs.add(job);
			job.addTechnology(this);
		}
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Technology) {
			Technology technology = (Technology) o;
			return Objects.equals(this.getName(), technology.getName());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("ID: %d|Tech: %s|Job count: %d", this.getId(), this.getName(), this.jobs.size());
	}

	@Override
	public DatabaseEntity generateFields() {
		this.setName(DatabaseEntity.generateRandomString());
		return this;
	}
}
