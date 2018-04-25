package com.github.openwhale.spritz;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SampleTest extends TestCase {
	
	public SampleTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(SampleTest.class);
	}

	public void testMain() {
		assertTrue(true);
	}
}
