/*
 * Copyright 2006 the original author or authors.
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
import static junit.framework.Assert.assertFalse;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.Result;
import org.junit.runner.notification.RunNotifier;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import static org.unitils.TracingTestListener.*;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.util.ReflectionUtils;

import java.util.Iterator;

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
public class UnitilsInvocationExceptionTest {


    /* Listener that records all method invocations during the tests */
    private static TracingTestListener tracingTestListener;

    /* Temporary holder so that the test listener that was replaced during the test can be place back */
    private static TestListener oldTestListenerUnitilsJUnit3;

    /* Temporary holder so that the test listener that was replaced during the test can be place back */
    private static TestListener oldTestListenerUnitilsJUnit4;


    /**
     * Sets up the test by installing the tracing test listener that will record all method invocations during the test.
     * The current test listeners are stored so that they can be restored during the class tear down.
     */
    @BeforeClass
    public static void classSetUp() {
        oldTestListenerUnitilsJUnit3 = (TestListener) ReflectionUtils.getFieldValue(null, ReflectionUtils.getFieldWithName(UnitilsJUnit3.class, "testListener", true));
        oldTestListenerUnitilsJUnit4 = (TestListener) ReflectionUtils.getFieldValue(null, ReflectionUtils.getFieldWithName(UnitilsJUnit4TestClassRunner.class, "testListener", true));

        InjectionUtils.injectStatic(null, UnitilsJUnit3.class, "testListener");
        InjectionUtils.injectStatic(null, UnitilsJUnit4TestClassRunner.class, "testListener");

        tracingTestListener = new TracingTestListener();

        UnitilsJUnit3Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit4Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_TestClass1.setTracingTestListener(tracingTestListener);
    }


    /**
     * This will put back the old test listeners that were replaced by the tracing test listener.
     */
    @AfterClass
    public static void classTearDown() {

        InjectionUtils.injectStatic(oldTestListenerUnitilsJUnit3, UnitilsJUnit3.class, "testListener");
        InjectionUtils.injectStatic(oldTestListenerUnitilsJUnit4, UnitilsJUnit4TestClassRunner.class, "testListener");
    }


    /**
     * Sets up the test by clearing the previous recorded method invocations. This will also re-initiliaze
     * the base-classes so that, for example beforeAll() will be called another time.
     */
    @Before
    public void setUp() throws Exception {

        tracingTestListener.getCallList().clear();

        // clear state so that beforeAll is called
        InjectionUtils.injectStatic(false, UnitilsJUnit3.class, "beforeAllCalled");
        InjectionUtils.injectStatic(null, UnitilsJUnit3.class, "lastTestClass");
        InjectionUtils.injectStatic(false, UnitilsJUnit4TestClassRunner.class, "beforeAllCalled");
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeAll} call of a module.
     *
     * @see #assertInvocationOrder_beforeAll
     */
    @Test
    public void testUnitilsJUnit3_beforeAll_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeAll("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(1, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#beforeAll} call of a module.
     *
     * @see #assertInvocationOrder_beforeAll
     */
    @Test
    public void testUnitilsJUnit3_beforeAll_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeAll("JUnit3", tracingTestListener);
        assertEquals(1, result.failureCount());       // failures instead of errors
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeAll} call of a module.
     *
     * @see #assertInvocationOrder_beforeAll
     */
    @Test
    public void testUnitilsJUnit4_beforeAll() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_beforeAll("JUnit4", tracingTestListener);
        assertEquals(0, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeAll} call of a module.
     *
     * @see #assertInvocationOrder_beforeAll
     */
    @Test
    public void testUnitilsTestNG_beforeAll() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_beforeAll("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestClass} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestClass
     */
    @Test
    public void testUnitilsJUnit3_beforeTestClass_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeTestClass("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#beforeTestClass} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestClass
     */
    @Test
    public void testUnitilsJUnit3_beforeTestClass_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeTestClass("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());   // failures instead of errors
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestClass} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestClass
     */
    @Test
    public void testUnitilsJUnit4_beforeTestClass() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_beforeTestClass("JUnit4", tracingTestListener);
        assertEquals(0, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestClass} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestClass
     */
    @Test
    public void testUnitilsTestNG_beforeTestClass() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_beforeTestClass("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }

    // no JUnit 3 versions of testBeforeClass (does not exist for JUnit3)

    /**
     * Test the flow when a runtime exception is thrown during a {@link org.junit.BeforeClass} call of a test.
     *
     * @see #assertInvocationOrder_testBeforeClass
     */
    @Test
    public void testUnitilsJUnit4_testBeforeClass() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_BEFORE_CLASS, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_testBeforeClass("JUnit4", tracingTestListener);
        assertEquals(0, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(0, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link org.testng.annotations.BeforeClass} call of a test.
     *
     * @see #assertInvocationOrder_testBeforeClass
     */
    @Test
    public void testUnitilsTestNG_testBeforeClass() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_BEFORE_CLASS, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_testBeforeClass("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestSetUp
     */
    @Test
    public void testUnitilsJUnit3_beforeTestSetUp_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeTestSetUp("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestSetUp
     */
    @Test
    public void testUnitilsJUnit3_beforeTestSetUp_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeTestSetUp("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestSetUp
     */
    @Test
    public void testUnitilsJUnit4_beforeTestSetUp() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_beforeTestSetUp("JUnit4", tracingTestListener);
        assertEquals(0, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestSetUp
     */
    @Test
    public void testUnitilsTestNG_beforeTestSetUp() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_beforeTestSetUp("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link junit.framework.TestCase#setUp} call of a test.
     *
     * @see #assertInvocationOrder_testSetUp
     */
    @Test
    public void testUnitilsJUnit3_testSetUp_RuntimeException() {

        tracingTestListener.setExceptionMethod(TEST_SET_UP, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_testSetUp("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link junit.framework.TestCase#setUp} call of a test.
     *
     * @see #assertInvocationOrder_testSetUp
     */
    @Test
    public void testUnitilsJUnit3_testSetUp_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TEST_SET_UP, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_testSetUp("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link org.junit.Before} call of a test.
     *
     * @see #assertInvocationOrder_testSetUp
     */
    @Test
    public void testUnitilsJUnit4_testSetUp() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_SET_UP, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_testSetUp("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link org.testng.annotations.BeforeMethod} call of a test.
     *
     * @see #assertInvocationOrder_testSetUp
     */
    @Test
    public void testUnitilsTestNG_testSetUp() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_SET_UP, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_testSetUp("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestMethod
     */
    @Test
    public void testUnitilsJUnit3_beforeTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeTestMethod("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestMethod
     */
    @Test
    public void testUnitilsJUnit3_beforeTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_beforeTestMethod("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());  // failures instead of errors
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestMethod
     */
    @Test
    public void testUnitilsJUnit4_beforeTestMethod() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_beforeTestMethod("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @see #assertInvocationOrder_beforeTestMethod
     */
    @Test
    public void testUnitilsTestNG_beforeTestMethod() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_beforeTestMethod("TestNG", tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit3_testMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(TEST_METHOD, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit3_testMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TEST_METHOD, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit4_testMethod() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_METHOD, false);
        Result result = performJUnit4Test();

        assertInvocationOrder("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsTestNG_testMethod() {

        tracingTestListener.setExceptionMethod(TEST_METHOD, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder("TestNG", tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestMethod} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit3_afterTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#afterTestMethod} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit3_afterTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestMethod} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit4_afterTestMethod() throws Exception {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);
        Result result = performJUnit4Test();

        assertInvocationOrder("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestMethod} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsTestNG_afterTestMethod() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder("TestNG", tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link junit.framework.TestCase#tearDown} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit3_testTearDown_RuntimeException() {

        tracingTestListener.setExceptionMethod(TEST_TEAR_DOWN, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_testTearDown("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link junit.framework.TestCase#tearDown} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit3_testTearDown_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TEST_TEAR_DOWN, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_testTearDown("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link org.junit.After} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit4_testTearDown() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_TEAR_DOWN, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_testTearDown("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link org.testng.annotations.AfterMethod} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsTestNG_testTearDown() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_TEAR_DOWN, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_testTearDown("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @see #assertInvocationOrder_afterTestTearDown
     */
    @Test
    public void testUnitilsJUnit3_afterTestTearDown_RuntimeException() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_afterTestTearDown("JUnit3", tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * Test the flow when an assertion error is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @see #assertInvocationOrder_afterTestTearDown
     */
    @Test
    public void testUnitilsJUnit3_afterTestTearDown_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder_afterTestTearDown("JUnit3", tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @see #assertInvocationOrder_afterTestTearDown
     */
    @Test
    public void testUnitilsJUnit4_afterTestTearDown() throws Exception {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);
        Result result = performJUnit4Test();

        assertInvocationOrder_afterTestTearDown("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(2, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @see #assertInvocationOrder_afterTestTearDown
     */
    @Test
    public void testUnitilsTestNG_afterTestTearDown() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder_afterTestTearDown("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }

    // no JUnit 3 versions of testAfterClass (does not exist for JUnit3)

    /**
     * Test the flow when a runtime exception is thrown during a {@link org.junit.AfterClass} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit4_testAfterClass() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_AFTER_CLASS, false);
        Result result = performJUnit4Test();

        assertInvocationOrder("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link org.testng.annotations.AfterClass} call of a test.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsTestNG_testAfterClass() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_AFTER_CLASS, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestClass} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsJUnit4_afterTestClass() throws Exception {

        tracingTestListener.setExceptionMethod(AFTER_TEST_CLASS, false);
        Result result = performJUnit4Test();

        assertInvocationOrder("JUnit4", tracingTestListener);
        assertEquals(2, result.getRunCount());
        assertEquals(1, result.getFailureCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterTestClass} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsTestNG_afterTestClass() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_CLASS, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Test the flow when a runtime exception is thrown during a {@link TestListener#afterAll} call of a module.
     *
     * @see #assertInvocationOrder
     */
    @Test
    public void testUnitilsTestNG_afterAll() {

        tracingTestListener.setExceptionMethod(AFTER_ALL, false);
        TestListenerAdapter testListenerAdapter = performTestNGTest();

        assertInvocationOrder("TestNG", tracingTestListener);
        assertEquals(1, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#beforeAll} call of a module.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_beforeAll(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        if ("JUnit3".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#beforeTestClass} call of a module.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_beforeTestClass(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        if ("JUnit3".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());  // 2 times, once for each test
            // The last afterTestClass will be called when the runtime exits
            // afterAll will be called when the runtime exits
        }
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a before class call of a test.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_testBeforeClass(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        // does not exist for JUnit3
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            // setup and teardown are still called even though tests are skipped
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());   // difference with JUnit
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // difference with JUnit
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());   // difference with JUnit
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // difference with JUnit
            // testAfterClass is skipped                                                 // difference with JUnit
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#beforeTestSetUp} call of a module.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_beforeTestSetUp(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        if ("JUnit3".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            // testBeforeClass does not exist
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // still called
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // still called
            // testAfterClass does not exist
            // The last afterTestClass will be called when the runtime exits
            // afterAll will be called when the runtime exits
        }
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // still called
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // still called
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // still called
            // second beforeTestSetUp is skipped                                         // difference with JUnit
            // second afterTestTearDown is skipped                                       // difference with JUnit
            // testAfterClass is skipped                                                 // difference with JUnit
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a setUp call of a test.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_testSetUp(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        if ("JUnit3".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            // testBeforeClass does not exist
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            // testTearDown is skipped                                                  // difference with JUnit4
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            // testTearDown is skipped                                                  // difference with JUnit4
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            // testAfterClass does not exist
            // The last afterTestClass will be called when the runtime exits
            // afterAll will be called when the runtime exits
        }
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());   // still called
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());   // still called
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            // testTearDown is skipped                                                  // difference with JUnit4
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            // second testSetUp is skipped                                              // difference with JUnit
            // testTearDown is skipped                                                  // difference with JUnit4
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            // testAfterClass is skipped                                                 // difference with JUnit
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#beforeTestMethod} call of a module.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_beforeTestMethod(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        if ("JUnit3".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            // testBeforeClass does not exist
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());  // still called
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());  // still called
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            // testAfterClass does not exist
            // The last afterTestClass will be called when the runtime exits
            // afterAll will be called when the runtime exits
        }
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());  // still called
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());  // still called
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());  // still called
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());  // still called
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the main flow of invocation calls.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder(String type, TracingTestListener tracingTestListener) {

        Iterator iterator = tracingTestListener.getCallList().iterator();

        if ("JUnit3".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            // testBeforeClass does not exist
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test2", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            // testAfterClass does not exist
            // The last afterTestClass will be called when the runtime exits
            // afterAll will be called when the runtime exits
        }
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test2", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            // afterAll will be called when the runtime exits
        }
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test2", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a tear down call of a test.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_testTearDown(String type, TracingTestListener tracingTestListener) {

        if ("JUnit3".equals(type) || "JUnit4".equals(type)) {
            assertInvocationOrder(type, tracingTestListener);
            return;
        }

        Iterator iterator = tracingTestListener.getCallList().iterator();
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());   // difference with JUnit
            // second testSetUp is skipped                                               // difference with JUnit
            // second beforeTestMethod is skipped                                        // difference with JUnit
            // second testMethod 2 is skipped                                            // difference with JUnit
            // second afterTestMethod is skipped                                         // difference with JUnit
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());   // difference with JUnit
            // testAfterClass is skipped                                                 // difference with JUnit
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Asserts the flow when an exception is thrown during a {@link TestListener#afterTestTearDown} call of a module.
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the recorded invocations
     */
    private void assertInvocationOrder_afterTestTearDown(String type, TracingTestListener tracingTestListener) {

        if ("JUnit3".equals(type) || "JUnit4".equals(type)) {
            assertInvocationOrder(type, tracingTestListener);
            return;
        }

        Iterator iterator = tracingTestListener.getCallList().iterator();
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] beforeAll", iterator.next());
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testMethod        - TestClass1 - test1", iterator.next());
            assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
            // second beforeTestSetUp is skipped                                         // difference with JUnit
            // second testSetUp is skipped                                               // difference with JUnit
            // second beforeTestMethod is skipped                                        // difference with JUnit
            // second testMethod 2 is skipped                                            // difference with JUnit
            // second afterTestMethod is skipped                                         // difference with JUnit
            // testAfterClass is skipped                                                 // difference with JUnit
            assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
    }


    /**
     * Runs the {@link UnitilsJUnit4Test_TestClass1} JUnit 4 test.
     *
     * @return the test result
     */
    private Result performJUnit4Test() throws Exception {
        Result result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);
        return result;
    }


    /**
     * Runs the {@link UnitilsTestNGTest_TestClass1} TestNG test.
     *
     * @return the test result
     */
    private TestListenerAdapter performTestNGTest() {
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();
        return testListenerAdapter;
    }


    /**
     * Overridden test class runner to be able to use the {@link TracingTestListener} as test listener.
     */
    private class TestUnitilsJUnit4TestClassRunner extends UnitilsJUnit4TestClassRunner {

        public TestUnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        protected Unitils getUnitils() {

            return new Unitils() {

                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
    }

}
