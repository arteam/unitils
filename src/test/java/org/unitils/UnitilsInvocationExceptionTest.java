/*
 * Copyright 2006-2007,  Unitils.org
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
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.unitils.TracingTestListener.ListenerInvocation.*;
import org.unitils.TracingTestListener.TestFramework;
import static org.unitils.TracingTestListener.TestFramework.*;
import static org.unitils.TracingTestListener.TestInvocation.*;
import org.unitils.core.TestListener;
import org.unitils.inject.util.InjectionUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Test for the flows in case an exception occurs in one of the listener or test methods for JUnit3 ({@link UnitilsJUnit3}),
 * JUnit4 (@link UnitilsJUnit4TestClassRunner}) and TestNG ({@link UnitilsTestNG}).
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same (see assertInvocationOrder* methods).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsJUnit3Test_TestClass1
 * @see UnitilsJUnit4Test_TestClass1
 * @see UnitilsTestNGTest_TestClass1
 */
@RunWith(Parameterized.class)
public class UnitilsInvocationExceptionTest extends UnitilsInvocationTestBase {

    Class<?> testClass;

    public UnitilsInvocationExceptionTest(TestFramework testFramework, TestExecutor testExecutor, Class<?> testClass) {
        super(testFramework, testExecutor);
        this.testClass = testClass;
    }

    @Parameters
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {JUNIT3, new JUnit3TestExecutor(), UnitilsJUnit3Test_TestClass1.class},
                {JUNIT4, new JUnit4TestExecutor(), UnitilsJUnit4Test_TestClass1.class},
                {TESTNG, new TestNGTestExecutor(), UnitilsTestNGTest_TestClass1.class},
                //{JUNIT3, new JUnit3TestExecutor(), SpringUnitilsJUnit38Test_TestClass1.class},
                //{JUNIT4, new JUnit4TestExecutor(), SpringUnitilsJUnit4Test_TestClass1.class},
                //{TESTNG, new TestNGTestExecutor(), SpringUnitilsTestNGTest_TestClass1.class},
        });
    }
    
    @Before
    public void resetJunit3() {
    	InjectionUtils.injectIntoStatic(null, UnitilsJUnit3.class, "currentTestClass");
    }

    /**
     * Test the flow when a runtime exception is thrown during a {@link org.junit.BeforeClass} call of a test.
     *
     * @see #assertInvocationOrder_testBeforeClass
     */
    @Test
    public void testTestBeforeClass() throws Exception {
        Assume.assumeTrue(!JUNIT3.equals(testFramework));

        tracingTestListener.expectExceptionInMethod(TEST_BEFORE_CLASS, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testBeforeClass();

        if (JUNIT4.equals(testFramework)) assertEquals(1, testExecutor.getFailureCount());
        if (TESTNG.equals(testFramework)) {
            assertEquals(0, testExecutor.getFailureCount());
            assertEquals(2, testExecutor.getIgnoreCount());
        }
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestSetUp
     */
    @Test
    public void testBeforeTestSetUp_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_BEFORE_TEST_SET_UP, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_beforeTestSetUp();

        if (TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getIgnoreCount());
        } else {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestSetUp
     */
    @Test
    public void testBeforeTestSetUp_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_BEFORE_TEST_SET_UP, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder_beforeTestSetUp();

        if (TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getIgnoreCount());
        } else {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link junit.framework.TestCase#setUp} call of a test.
     *
     * @see #assertInvocationOrder_testSetUp
     */
    @Test
    public void testTestSetUp_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(TEST_SET_UP, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testSetUp();

        if (TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getIgnoreCount());
        } else {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link junit.framework.TestCase#setUp} call of a test.
     *
     * @see #assertInvocationOrder_testSetUp
     */
    @Test
    public void testTestSetUp_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(TEST_SET_UP, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testSetUp();

        if (TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getIgnoreCount());
        } else {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestMethod
     */
    @Test
    public void testBeforeTestMethod_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_BEFORE_TEST_METHOD, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_beforeTestMethod();
        assertEquals(2, testExecutor.getFailureCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestMethod
     */
    @Test
    public void testBeforeTestMethod_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_BEFORE_TEST_METHOD, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder_beforeTestMethod();
        assertEquals(2, testExecutor.getFailureCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testTestMethod_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(TEST_METHOD, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testMethod();
        assertEquals(2, testExecutor.getFailureCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testTestMethod_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(TEST_METHOD, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testMethod();
        assertEquals(2, testExecutor.getFailureCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestMethod} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testAfterTestMethod_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_AFTER_TEST_METHOD, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder();
        assertEquals(2, testExecutor.getFailureCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#afterTestMethod} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testAfterTestMethod_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_AFTER_TEST_METHOD, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder();
        assertEquals(2, testExecutor.getFailureCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link junit.framework.TestCase#tearDown} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testTestTearDown_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(TEST_TEAR_DOWN, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testTearDown();
        if (!TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link junit.framework.TestCase#tearDown} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testTestTearDown_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(TEST_TEAR_DOWN, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder_testTearDown();
        if (!TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @see #assertInvocationOrder_afterTestTearDown
     */
    @Test
    public void testAfterTestTearDown_RuntimeException() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_AFTER_TEST_TEARDOWN, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder_afterTestTearDown();
        if (!TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @see #assertInvocationOrder_afterTestTearDown
     */
    @Test
    public void testAfterTestTearDown_AssertionFailedError() throws Exception {
        tracingTestListener.expectExceptionInMethod(LISTENER_AFTER_TEST_TEARDOWN, true);
        testExecutor.runTests(testClass);

        assertInvocationOrder_afterTestTearDown();
        if (!TESTNG.equals(testFramework)) {
            assertEquals(2, testExecutor.getFailureCount());
        }
    }

    /**
     * Test the flow when a runtime exception is thrown during a {@link org.junit.AfterClass} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testTestAfterClass() throws Exception {
        Assume.assumeTrue(!testFramework.equals(JUNIT3));

        tracingTestListener.expectExceptionInMethod(TEST_AFTER_CLASS, false);
        testExecutor.runTests(testClass);

        assertInvocationOrder();
    }


    /**
     * Asserts the flow when an exception is thrown during a before class call of a test.
     */
    private void assertInvocationOrder_testBeforeClass() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass);
        // TestNG calls @BeforeMethod and @AfterMethod methods, even though the tests themselves are skipped
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass, TESTNG);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass, TESTNG);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass, TESTNG);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass, TESTNG);
        // TestNG doesn't call the @AfterClass method because there was an exception in @BeforeClass
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4);
        assertNoMoreInvocations();
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     */
    private void assertInvocationOrder_beforeTestSetUp() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4);
        assertNoMoreInvocations();
    }


    /**
     * Asserts the flow when an exception is thrown during a setUp call of a test.
     */
    private void assertInvocationOrder_testSetUp() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_SET_UP, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_TEAR_DOWN, testClass, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass, JUNIT3, JUNIT4);
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     */
    private void assertInvocationOrder_beforeTestMethod() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
    	assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4, TESTNG);
        assertNoMoreInvocations();
    }


    /**
     * Asserts the flow when an exception is thrown during the test.
     */
    private void assertInvocationOrder_testMethod() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4, TESTNG);
        assertNoMoreInvocations();
    }

    /**
     * Asserts the flow when an exception is thrown during the test.
     */
    private void assertInvocationOrder() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4, TESTNG);
        assertNoMoreInvocations();
    }


    /**
     * Asserts the flow when an exception is thrown during a tear down call of a test.
     */
    private void assertInvocationOrder_testTearDown() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_METHOD, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_TEAR_DOWN, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4);
        assertNoMoreInvocations();
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     */
    private void assertInvocationOrder_afterTestTearDown() {
    	assertInvocation(LISTENER_BEFORE_CLASS, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, TESTNG);
        assertInvocation(TEST_BEFORE_CLASS, testClass, JUNIT4, TESTNG);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass);
        assertInvocation(TEST_SET_UP, testClass);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass);
        assertInvocation(TEST_METHOD, testClass);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass);
        assertInvocation(TEST_TEAR_DOWN, testClass);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass);
        assertInvocation(LISTENER_AFTER_CREATE_TEST_OBJECT, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_SET_UP, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_SET_UP, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_BEFORE_TEST_METHOD, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_METHOD, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_METHOD, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_TEAR_DOWN, testClass, JUNIT3, JUNIT4);
        assertInvocation(LISTENER_AFTER_TEST_TEARDOWN, testClass, JUNIT3, JUNIT4);
        assertInvocation(TEST_AFTER_CLASS, testClass, JUNIT4);
        assertNoMoreInvocations();
    }

}
