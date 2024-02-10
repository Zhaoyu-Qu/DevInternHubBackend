package com.Jason.DevInternHubBackend.domain;

public interface DatabaseEntity {
	DatabaseEntity generateFields();

	Long getId();

	static String generateRandomString(int size) {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			int index = (int) (AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	static String generateRandomString() {
		// String size by default is 15
		return generateRandomString(15);
	}
}
