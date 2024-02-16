package com.Jason.DevInternHubBackend.domain;

import java.util.Objects;
import jakarta.persistence.CascadeType;
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
	private Long id;
	private String title, description, url, location;
	private LocalDate openingDate, closingDate;
	private String specialisation; // backend, frontend, mobile development, etc.
	private String type; // graduate jobs, internships, entry level jobs, etc.
	private boolean verificationStatus = false;

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

	public AppUser getOwner() {
		return owner;
	}

	public boolean isVerificationStatus() {
		return verificationStatus;
	}

	public void setVerificationStatus(boolean verificationStatus) {
		this.verificationStatus = verificationStatus;
	}

	public Set<AppUser> getBookmarkHolders() {
		return bookmarkHolders;
	}
	
	public void addBookmarkHolder(AppUser bookmarkHolder) {
		this.bookmarkHolders.add(bookmarkHolder);
		if (!bookmarkHolder.getBookmarkedJobs().contains(this)) {
			bookmarkHolder.addBookmarkedjob(this);
		}
	}
	
	public void removedBookmarkHolder(AppUser bookmarkHolder) {
		if (this.bookmarkHolders.contains(bookmarkHolder))
			this.bookmarkHolders.remove(bookmarkHolder);
		if (bookmarkHolder.getBookmarkedJobs().contains(this))
			bookmarkHolder.removeBookmarkedJob(this);
	}

	public void setOwner(AppUser owner) {
		if (this.owner != null) {
			this.owner.getOwnedJobs().remove(this);
		}
		this.owner = owner;
		owner.addOwnedJob(this);
	}

	public void addTechnology(Technology technology) {
		if (!this.technologies.contains(technology)) {
			this.technologies.add(technology);
			technology.addJob(this);
		}
	}

	public Set<Technology> getTechnologies() {
		return technologies;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		if (this.company == null || !this.company.equals(company)) {
			this.company = company;
			company.addJob(this);
		}
	}

	public String getCompanyName() {
		if (getCompany() == null)
			return null;
		return getCompany().getCompanyName();
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

	public Long getId() {
		return id;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Job) {
			Job job = (Job) o;
			return Objects.equals(this.getTitle(), job.getTitle())
					&& Objects.equals(this.getDescription(), job.getDescription())
					&& Objects.equals(this.getLocation(), job.getLocation())
					&& Objects.equals(this.getSpecialisation(), job.getSpecialisation())
					&& Objects.equals(this.getType(), job.getType()) && Objects.equals(this.getUrl(), job.getUrl())
					&& Objects.equals(this.getClosingDate(), job.getClosingDate())
					&& Objects.equals(this.getOpeningDate(), job.getOpeningDate())
					&& Objects.equals(this.getTechnologies(), job.getTechnologies())
					&& Objects.equals(this.getUrl(), job.getUrl())
					&& Objects.equals(this.getCompanyName(), job.getCompanyName());
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("ID: %d|Title: %s|Company: %s", this.getId(), this.getTitle(),
				this.getCompany().getCompanyName());
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
