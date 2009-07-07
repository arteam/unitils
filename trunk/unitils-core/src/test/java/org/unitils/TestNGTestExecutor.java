/**
 * 
 */
package org.unitils;

import org.testng.TestListenerAdapter;
import org.testng.TestNG;

class TestNGTestExecutor implements TestExecutor {

	private TestListenerAdapter testListenerAdapter;
	
	public void runTests(Class<?>... testClasses) throws Exception {
		testListenerAdapter = new TestListenerAdapter();

        TestNG testng = new TestNG();
        testng.setTestClasses(testClasses);
        testng.addListener(testListenerAdapter);
        testng.run();
	}
	
	public void runTests(String testGroups, Class<?>... testClasses) throws Exception {
		testListenerAdapter = new TestListenerAdapter();

        TestNG testng = new TestNG();
        testng.setGroups(testGroups);
        testng.setTestClasses(testClasses);
        testng.addListener(testListenerAdapter);
        testng.run();
	}
	
	public int getRunCount() {
		return testListenerAdapter.getPassedTests().size() + 
			testListenerAdapter.getFailedTests().size() + 
			testListenerAdapter.getSkippedTests().size();
	}
	
	public int getFailureCount() {
		return testListenerAdapter.getFailedTests().size();
	}
	
	public int getIgnoreCount() {
		return testListenerAdapter.getSkippedTests().size();
	}
	
}