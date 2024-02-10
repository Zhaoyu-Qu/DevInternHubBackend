package com.Jason.DevInternHubBackend.domain;

public enum Role {
	ADMIN, USER, GUEST;
	
	public static Role fromString(String roleStr) throws IllegalArgumentException {
		try {
			return Role.valueOf(roleStr.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(roleStr + "is not a valid role.");
		}
	}
}
