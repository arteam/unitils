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
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
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
 * Test for {@link UnitilsJUnit3}.
 * <p/>
 * todo afterTestClass + afterAll explain
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
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeAll_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_ALL, tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }

    @Test
    public void testUnitilsJUnit3BeforeAll_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_ALL, tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     * <p/>
     * todo implement
     */
    @Test
    public void testUnitilsJUnit4BeforeAll() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", BEFORE_ALL, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     * <p/>
     * todo implement
     */
    @Test
    public void testUnitilsTestNGBeforeAll() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", BEFORE_ALL, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeTestClass_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_TEST_CLASS, tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeTestClass_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_TEST_CLASS, tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4BeforeTestClass() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", BEFORE_TEST_CLASS, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsTestNGBeforeTestClass() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", BEFORE_TEST_CLASS, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4TestBeforeClass() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_BEFORE_CLASS, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", TEST_BEFORE_CLASS, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsTestNGTestBeforeClass() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_BEFORE_CLASS, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", TEST_BEFORE_CLASS, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeTestSetUp_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_TEST_SET_UP, tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeTestSetUp_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_TEST_SET_UP, tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4BeforeTestSetUp() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", BEFORE_TEST_SET_UP, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsTestNGBeforeTestSetUp() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", BEFORE_TEST_SET_UP, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4TestSetUp() throws Exception {

        tracingTestListener.setExceptionMethod(TEST_SET_UP, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", TEST_SET_UP, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_TEST_METHOD, tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3BeforeTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", BEFORE_TEST_METHOD, tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4BeforeTestMethod() throws Exception {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", BEFORE_TEST_METHOD, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsTestNGBeforeTestMethod() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", BEFORE_TEST_METHOD, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3AfterTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", AFTER_TEST_METHOD, tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3AfterTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, true);
        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", AFTER_TEST_METHOD, tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4AfterTestMethod() throws Exception {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", AFTER_TEST_METHOD, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsTestNGAfterTestMethod() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", AFTER_TEST_METHOD, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3AfterTestTearDown_RuntimeException() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", AFTER_TEST_TEAR_DOWN, tracingTestListener);
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit3AfterTestTearDown_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        assertInvocationOrder("JUnit3", AFTER_TEST_TEAR_DOWN, tracingTestListener);
        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsJUnit4AfterTestTearDown() throws Exception {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);
        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);
        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        testRunner1.run(runNotifier);

        assertInvocationOrder("JUnit4", AFTER_TEST_TEAR_DOWN, tracingTestListener);
        assertEquals(2, failureRunListener.getFailureCount());
    }


    /**
     * todo javadoc
     */
    @Test
    public void testUnitilsTestNGAfterTestTearDown() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();
        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", AFTER_TEST_TEAR_DOWN, tracingTestListener);
        assertEquals(2, testListenerAdapter.getFailedTests().size());
    }


    /**
     * todo javadoc
     * <p/>
     * Asserts that the given listener recorded the correct invocation sequence. Except for some minor difference, the
     * sequence should be equal for  all test frameworks.
     * <p/>
     * Following difference are allowed:<ul>
     * <li>beforeTestClass and afterTestClass no not exist in JUnit 3 (the Unitils versions will be called however)</li>
     * </ul>
     * For JUnit3 and JUnit4 afterAll will be called during the runtime exit and can therefore not be asserted here.
     * The same is true for the last afterTestClass method of JUnit3 tests. This is because you cannot determine which test
     * is going to be the last test in the class
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param exceptionMethod     the location of the exception, not null
     * @param tracingTestListener the listener, not null
     */
    private void assertInvocationOrder(String type, String exceptionMethod, TracingTestListener tracingTestListener) {
        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());

        if (BEFORE_ALL.equals(exceptionMethod)) {
            assertEquals("[Unitils] beforeAll", iterator.next()); // 2 times, once for each test
            return;
        }

        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        if (BEFORE_TEST_CLASS.equals(exceptionMethod)) {
            assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());  // 2 times, once for each test
            return;
        }

        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
        }

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        if (!BEFORE_TEST_SET_UP.equals(exceptionMethod)) {
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
            if (!BEFORE_TEST_METHOD.equals(exceptionMethod)) {
                assertEquals("[Test]    testMethod        - TestClass1 - test1", iterator.next());
                assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
            }
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        }

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        if (!BEFORE_TEST_SET_UP.equals(exceptionMethod)) {
            assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
            assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
            if (!BEFORE_TEST_METHOD.equals(exceptionMethod)) {
                assertEquals("[Test]    testMethod        - TestClass1 - test2", iterator.next());
                assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
            }
            assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
            assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        }

        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
        }
        assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());

        // For JUnit 3 and JUnit 4 afterAll will be called when the runtime exits
        if ("TestNG".equals(type)) {
            assertEquals("[Unitils] afterAll", iterator.next());
        }
        assertFalse(iterator.hasNext());
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

    /**
     * JUnit 4 run listener for recording the nr of failures during the test.
     */
    private class FailureRunListener extends RunListener {

        private int failureCount = 0;

        public void testFailure(Failure failure) throws Exception {
            super.testFailure(failure);
            failureCount++;
        }

        public int getFailureCount() {
            return failureCount;
        }
    }

}
