package com.Jason.DevInternHubBackend.domain;

import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Job implements DatabaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false, unique = true)
	private Long id;
	@Column(nullable = false, unique = true)
	private String url;
	private String title, description, location;
	private LocalDate openingDate, closingDate;
	private String specialisation; // backend, frontend, mobile development, etc.
	private String type; // graduate jobs, internships, entry level jobs, etc.
	private Boolean isVerified;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "companyId")
	private Company company;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ownerId")
	private AppUser owner;

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "job_technology", joinColumns = @JoinColumn(name = "jobId"), inverseJoinColumns = @JoinColumn(name = "techId"))
	private Set<Technology> technologies = new HashSet<>();

	@ManyToMany
	@JoinTable(name = "job_bookmarkHolders", joinColumns = @JoinColumn(name = "jobId"), inverseJoinColumns = @JoinColumn(name = "bookmarkHolderId"))
	private Set<AppUser> bookmarkHolders = new HashSet<>();

	public Job() {
	}

	public Job(String title) {
		this.title = title;
	}

	public Job(String title, Company company) {
		this.title = title;
		this.company = company;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getOpeningDate() {
		return openingDate;
	}

	public void setOpeningDate(LocalDate openingDate) {
		this.openingDate = openingDate;
	}

	public LocalDate getClosingDate() {
		return closingDate;
	}

	public void setClosingDate(LocalDate closingDate) {
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

	public boolean setType(String type) {
		if (Objects.equals(type, "Graduate Job") || Objects.equals(type, "Internship")) {
			this.type = type;
			return true;
		} else
			return false;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public AppUser getOwner() {
		return owner;
	}

	public void setOwner(AppUser owner) {
		this.owner = owner;
	}

	public Set<Technology> getTechnologies() {
		return technologies;
	}

	public void setTechnologies(Set<Technology> technologies) {
		this.technologies = technologies;
	}

	public Set<AppUser> getBookmarkHolders() {
		return bookmarkHolders;
	}

	public void setBookmarkHolders(Set<AppUser> bookmarkHolders) {
		this.bookmarkHolders = bookmarkHolders;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Job) {
			Job job = (Job) o;
			return Objects.equals(this.getUrl(), job.getUrl());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(url);
	}

	@Override
	public DatabaseEntity generateFields() {
		this.setDescription(DatabaseEntity.generateRandomString());
		this.setLocation(DatabaseEntity.generateRandomString());
		this.setSpecialisation(DatabaseEntity.generateRandomString());
		this.setTitle(DatabaseEntity.generateRandomString());
		this.setType(DatabaseEntity.generateRandomString());
		this.setUrl(DatabaseEntity.generateRandomString());
		return this;
	}

}
