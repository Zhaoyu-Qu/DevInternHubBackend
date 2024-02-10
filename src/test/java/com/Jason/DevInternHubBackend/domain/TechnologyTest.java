package com.Jason.DevInternHubBackend.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TechnologyTest {
	@Test
	public void testEquals() {
		Technology t1, t2;
		t1 = new Technology();
		t2 = new Technology();
		assertTrue(t1.equals(t2));
		t1.setName("foo");
		assertFalse(t1.equals(t2));
		t2.setName("foo");
		assertTrue(t1.equals(t2));
	}
}
