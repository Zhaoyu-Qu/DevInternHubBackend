package com.Jason.DevInternHubBackend.domain;

public class JobUpdateDto extends JobBaseDto {
	private Boolean isVerified;
	public Boolean getIsVerified() {
		return isVerified;
	}
	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}
}
