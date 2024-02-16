package com.Jason.DevInternHubBackend.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class AppUser implements DatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING) // This tells JPA to store the enum values as strings
	private Role role;

	@JsonIgnore
	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "owner")
	private List<Job> ownedJobs = new ArrayList<Job>();

	@JsonIgnore
	@ManyToMany(mappedBy = "bookmarkHolders")
	private Set<Job> bookmarkedJobs = new HashSet<Job>();

	public AppUser() {
	}

	private AppUser(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public AppUser(String username, String password, String role) {
		this(username, password);
		this.setRole(role);
	}

	public AppUser(String username, String password, Role role) {
		this(username, password);
		this.setRole(role);
	}

	public Set<Job> getBookmarkedJobs() {
		return bookmarkedJobs;
	}

	public void removeBookmarkedJob(Job job) {
		if (this.bookmarkedJobs.contains(job))
			this.bookmarkedJobs.remove(job);
		if (job.getBookmarkHolders().contains(this))
			job.removedBookmarkHolder(this);

	}

	public void addBookmarkedjob(Job job) {
		this.bookmarkedJobs.add(job);
		if (!job.getBookmarkHolders().contains(this)) {
			job.addBookmarkHolder(this);
		}
	}

	public List<Job> getOwnedJobs() {
		return ownedJobs;
	}

	public void addOwnedJob(Job job) {
		if (!this.ownedJobs.contains(job)) {
			this.ownedJobs.add(job);
		}
		if (job.getOwner() == null || !job.getOwner().equals(this)) {
			job.setOwner(this);
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return this.role;
	}

	public void setRole(String role) {
		this.role = Role.fromString(role);
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AppUser) {
			AppUser user = (AppUser) o;
			return Objects.equals(this.getUsername(), user.getUsername());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("ID: %d|Username: %s|Role: %s", this.getId(), this.getUsername(),
				this.getRole().toString());
	}

	@Override
	public DatabaseEntity generateFields() {
		this.setUsername(DatabaseEntity.generateRandomString());
		this.setPassword(DatabaseEntity.generateRandomString());
		this.setRole(Role.GUEST);
		return this;
	}
}
