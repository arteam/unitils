package org.unitils;

import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;

public class JUnit4BlockTestExecutor implements TestExecutor {

	private Result result;

	JUnit4BlockTestExecutor() {
		super();
	}

	public void runTests(Class<?>... testClasses) throws Exception {
		result = new Result();
		RunNotifier runNotifier = new RunNotifier();
		runNotifier.addListener(result.createListener());

		for (Class<?> testClass : testClasses) {
			UnitilsJUnit4BlockTestClassRunner testClassRunner = new UnitilsJUnit4BlockTestClassRunner(
					testClass);
			testClassRunner.run(runNotifier);
		}
	}

	public void runTests(String testGroup, Class<?>... testClasses)
			throws Exception {
		runTests(testClasses);
	}

	public int getRunCount() {
		return result.getRunCount();
	}

	public int getFailureCount() {
		return result.getFailureCount();
	}

	public int getIgnoreCount() {
		return result.getIgnoreCount();
	}

}
