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

import static junit.framework.Assert.assertEquals;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_CREATE_TEST_OBJECT;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_TEST_METHOD;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_AFTER_TEST_TEARDOWN;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_CLASS;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_TEST_METHOD;
import static org.unitils.TracingTestListener.ListenerInvocation.LISTENER_BEFORE_TEST_SET_UP;
import static org.unitils.TracingTestListener.TestFramework.JUNIT3;
import static org.unitils.TracingTestListener.TestFramework.JUNIT4;
import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.unitils.TracingTestListener.TestFramework;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.inject.util.InjectionUtils;

/**
 * Test for the main flow of the unitils test listeners for JUnit3 (
 * {@link UnitilsJUnit3}), JUnit4 ({@link UnitilsJUnit4TestClassRunner},
 * {@link UnitilsJUnit4BlockTestClassRunner}) and TestNG.
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same.
 * <p/>
 * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and
 * EmptyTestClass that does not contain any methods. TestClass1 also contains an
 * ignored test (not for JUnit3). TestClass3 is the same as TestClass1 but does
 * not specify any before and after methods (JUnit4 only).
 * 
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsJUnit3Test_TestClass1
 * @see UnitilsJUnit3Test_TestClass2
 * @see UnitilsJUnit3Test_EmptyTestClass
 * @see UnitilsJUnit4Test_TestClass1
 * @see UnitilsJUnit4Test_TestClass2
 * @see UnitilsJUnit4Test_TestClassWithoutBeforeAndAfterMethods
 */
@RunWith(Parameterized.class)
public class JUnitUnitilsInvocationTest extends UnitilsInvocationTestBase {

	private Class<?> testClass1, testClass2, testClass3;

	public JUnitUnitilsInvocationTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass1, Class<?> testClass2, Class<?> testClass3) {
		super(testFramework, testExecutor);
		this.testClass1 = testClass1;
		this.testClass2 = testClass2;
		this.testClass3 = testClass3;
	}

	@Parameters
	public static Collection<Object[]> testData() {
		return Arrays.asList(new Object[][] {
				{ JUNIT3, new JUnit3TestExecutor(), UnitilsJUnit3Test_TestClass1.class, UnitilsJUnit3Test_TestClass2.class,
						UnitilsJUnit3Test_TestClassWithoutSetupAndTearDownMethods.class },
				{ JUNIT4, new JUnit4TestExecutor(), UnitilsJUnit4Test_TestClass1.class, UnitilsJUnit4Test_TestClass2.class,
						UnitilsJUnit4Test_TestClassWithoutBeforeAndAfterMethods.class },
				{ JUNIT4, new JUnit4BlockTestExecutor(), UnitilsJUnit4Test_TestClass1.class, UnitilsJUnit4Test_TestClass2.class,
						UnitilsJUnit4Test_TestClassWithoutBeforeAndAfterMethods.class },
		// {JUNIT3, new JUnit3TestExecutor(),
				// SpringUnitilsJUnit38Test_TestClass1.class,
				// SpringUnitilsJUnit38Test_TestClass2.class},
				// {JUNIT4, new JUnit4TestExecutor(),
				// SpringUnitilsJUnit4Test_TestClass1.class,
				// SpringUnitilsJUnit4Test_TestClass2.class},
				});
	}

	@Before
	public void resetJunit3() {
		UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(tracingTestListener);
		InjectionUtils.injectIntoStatic(null, UnitilsJUnit3.class, "currentTestClass");
	}

	@After
	public void tearDown() {
		UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(null);
	}

	@Test
	public void testSuccessfulRun() throws Exception {
		testExecutor.runTests(testClass1, testClass2);
		assertInvocationOrder(testClass1, testClass2);
	}

	@Test
	public void testSuccessfulRunWithoutBeforeAndAftersMethods() throws Exception {
		testExecutor.runTests(testClass3);
		assertInvocation(LISTENER_BEFORE_CLASS, testClass3, JUNIT3, JUNIT4);
		assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass3, JUNIT3, JUNIT4);
		// testClass 1, testMethod 1
		assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass3, JUNIT3, JUNIT4);
		assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass3, JUNIT3, JUNIT4);
		assertInvocation(TEST_METHOD, testClass3);
		assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass3, JUNIT3, JUNIT4);
		assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass3, JUNIT3, JUNIT4);
		// testClass 1, testMethod 2
		assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass3, JUNIT3, JUNIT4);
		assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass3, JUNIT3, JUNIT4);
		assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass3, JUNIT3, JUNIT4);
		assertInvocation(TEST_METHOD, testClass3);
		assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass3, JUNIT3, JUNIT4);
		assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass3, JUNIT3, JUNIT4);

		assertEquals(2, testExecutor.getRunCount());
		assertEquals(0, testExecutor.getFailureCount());
	}

	/**
	 * JUnit 3 test class without any tests. Inner class to avoid a failing
	 * test.
	 */
	protected static class UnitilsJUnit3Test_EmptyTestClass extends UnitilsJUnit3 {

		private static TracingTestListener tracingTestListener;

		public static void setTracingTestListener(TracingTestListener testListener) {
			tracingTestListener = testListener;
		}

		@Override
		protected Unitils getUnitils() {
			if (tracingTestListener != null) {
				return new Unitils() {

					@Override
					public TestListener getTestListener() {
						return tracingTestListener;
					}
				};
			}
			return super.getUnitils();
		}
	}

}
