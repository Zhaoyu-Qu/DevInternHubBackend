package com.Jason.DevInternHubBackend.domain;

public class JobResponseDto extends JobBaseDto {
	private Long id;
	private Boolean isVerified;
	private Boolean isBookmarked;
	private Boolean isOwned;
	private String companyUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public Boolean getIsBookmarked() {
		return isBookmarked;
	}

	public void setIsBookmarked(Boolean isBookmarked) {
		this.isBookmarked = isBookmarked;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public Boolean getIsOwned() {
		return isOwned;
	}

	public void setIsOwned(Boolean isOwned) {
		this.isOwned = isOwned;
	}
	
}
