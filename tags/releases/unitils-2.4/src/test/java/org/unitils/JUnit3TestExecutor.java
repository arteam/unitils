/*
 * Copyright 2008,  Unitils.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


class JUnit3TestExecutor implements TestExecutor {
	
	private TestResult result;

	JUnit3TestExecutor() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	public void runTests(Class<?>... testClasses) throws Exception {
		TestSuite suite = new TestSuite();
        for (Class<?> testClass : testClasses) {
        	Class<? extends TestCase> junit3TestClass = (Class<? extends TestCase>) testClass;
        	suite.addTestSuite(junit3TestClass);
        }
        TestRunner testRunner = new TestRunner();
        result = testRunner.doRun(suite);
	}
	
	public void runTests(String testGroup, Class<?>... testClasses)
			throws Exception {
		runTests(testClasses);
	}

	public int getRunCount() {
		return result.runCount();
	}

	public int getFailureCount() {
		return result.errorCount() + result.failureCount();
	}

	public int getIgnoreCount() {
		return 0;
	}
}