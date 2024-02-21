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
import jakarta.persistence.OneToMany;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class AppUser implements DatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false, unique = true)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING) // This tells JPA to store the enum values as strings
	private Role role;

	@JsonIgnore
	@OneToMany(mappedBy = "owner")
	private Set<Job> ownedJobs = new HashSet<Job>();

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public void setRole(String role) {
		this.role = Role.fromString(role);
	}

	public Set<Job> getOwnedJobs() {
		return ownedJobs;
	}

	public void setOwnedJobs(Set<Job> ownedJobs) {
		this.ownedJobs = ownedJobs;
	}

	public Set<Job> getBookmarkedJobs() {
		return bookmarkedJobs;
	}

	public void setBookmarkedJobs(Set<Job> bookmarkedJobs) {
		this.bookmarkedJobs = bookmarkedJobs;
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
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public DatabaseEntity generateFields() {
		this.setUsername(DatabaseEntity.generateRandomString());
		this.setPassword(DatabaseEntity.generateRandomString());
		this.setRole(Role.GUEST);
		return this;
	}
}
