package com.Jason.DevInternHubBackend.domain;

public class JobUpdateDto extends JobBaseDto {
	private Boolean isVerified;
	private Boolean isBookmarked;
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
}
