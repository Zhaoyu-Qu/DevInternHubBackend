package com.Jason.DevInternHubBackend.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Technology implements DatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false, unique = true)
	private Long id;
	@Column(nullable = false, unique = true)
	private String name;
	@JsonIgnore
	@ManyToMany(mappedBy = "technologies")
	private Set<Job> jobs = new HashSet<Job>();

	public Technology() {
	}

	public Technology(String technology) {
		this.name = technology;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public void setJobs(Set<Job> jobs) {
		this.jobs = jobs;
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
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public DatabaseEntity generateFields() {
		this.setName(DatabaseEntity.generateRandomString());
		return this;
	}
}
