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
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import static org.unitils.inject.util.InjectionUtils.injectStatic;
import org.unitils.util.ReflectionUtils;

import java.util.Iterator;

/**
 * Test for the main flow of the unitils test listeners for JUnit3 ({@link UnitilsJUnit3}),
 * JUnit4 (@link UnitilsJUnit4TestClassRunner}) and TestNG ({@link UnitilsTestNG}).
 * <p/>
 * Except for some minor differences, the flows for all these test frameworks
 * are expected to be the same (see {@link #assertInvocationOrder}.
 * <p/>
 * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
 * that does not contain any methods. TestClass1 also contains an ignored test (not for JUnit3).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @see UnitilsJUnit3Test_TestClass1
 * @see UnitilsJUnit3Test_TestClass2
 * @see UnitilsJUnit3Test_EmptyTestClass
 * @see UnitilsJUnit4Test_TestClass1
 * @see UnitilsJUnit4Test_TestClass2
 * @see UnitilsTestNGTest_TestClass1
 * @see UnitilsTestNGTest_TestClass2
 * @see UnitilsTestNGTest_EmptyTestClass
 */
public class UnitilsInvocationTest {

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
    public static void classSetup() {
        oldTestListenerUnitilsJUnit3 = (TestListener) ReflectionUtils.getFieldValue(null, ReflectionUtils.getFieldWithName(UnitilsJUnit3.class, "testListener", true));
        oldTestListenerUnitilsJUnit4 = (TestListener) ReflectionUtils.getFieldValue(null, ReflectionUtils.getFieldWithName(UnitilsJUnit4TestClassRunner.class, "testListener", true));

        injectStatic(null, UnitilsJUnit3.class, "testListener");
        injectStatic(null, UnitilsJUnit4TestClassRunner.class, "testListener");

        tracingTestListener = new TracingTestListener();

        UnitilsJUnit3Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(tracingTestListener);

        UnitilsJUnit4Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit4Test_TestClass2.setTracingTestListener(tracingTestListener);

        UnitilsTestNGTest_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_GroupsTest.setTracingTestListener(tracingTestListener);
    }


    /**
     * This will put back the old test listeners that were replaced by the tracing test listener.
     */
    @AfterClass
    public static void classTearDown() {
        injectStatic(oldTestListenerUnitilsJUnit3, UnitilsJUnit3.class, "testListener");
        injectStatic(oldTestListenerUnitilsJUnit4, UnitilsJUnit4TestClassRunner.class, "testListener");
    }


    /**
     * Sets up the test by clearing the previous recorded method invocations. This will also re-initiliaze
     * the base-classes so that, for example beforeAll() will be called another time.
     */
    @Before
    public void setUp() throws Exception {
        tracingTestListener.getCallList().clear();

        // clear state so that beforeAll is called
        injectStatic(false, UnitilsJUnit3.class, "beforeAllCalled");
        injectStatic(null, UnitilsJUnit3.class, "lastTestClass");
        injectStatic(false, UnitilsJUnit4TestClassRunner.class, "beforeAllCalled");

    }


    /**
     * Tests the correct invocation sequence of listener methods for a JUnit3 test.
     */
    @Test
    public void testUnitilsJUnit3() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(UnitilsJUnit3Test_TestClass1.class);
        suite.addTestSuite(UnitilsJUnit3Test_TestClass2.class);
        suite.addTestSuite(UnitilsJUnit3Test_EmptyTestClass.class);

        TestRunner testRunner = new TestRunner();
        TestResult testResult = testRunner.doRun(suite);

        assertInvocationOrder("JUnit3", tracingTestListener);
        // EmptyTestClass has caused a failure and will not be run
        assertEquals(0, testResult.errorCount());
        assertEquals(1, testResult.failureCount());
    }


    /**
     * Tests the correct invocation sequence of listener methods for a JUnit4 test.
     */
    @Test
    public void testUnitilsJUnit4() throws Exception {

        Result result = new Result();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(result.createListener());

        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        TestUnitilsJUnit4TestClassRunner testRunner2 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass2.class);
        testRunner1.run(runNotifier);
        testRunner2.run(runNotifier);

        assertInvocationOrder("JUnit4", tracingTestListener);
        assertEquals(4, result.getRunCount());
        assertEquals(1, result.getIgnoreCount());
    }


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test.
     */
    @Test
    public void testUnitilsTestNG() {
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();

        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class, UnitilsTestNGTest_TestClass2.class, UnitilsTestNGTest_EmptyTestClass.class});
        testng.addListener(testListenerAdapter);
        testng.run();

        assertInvocationOrder("TestNG", tracingTestListener);
        assertEquals(0, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test that defines a test group.
     */
    @Test
    public void testUnitilsTestNG_group() {
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();

        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_GroupsTest.class});
        testng.setGroups("testGroup");
        testng.addListener(testListenerAdapter);
        testng.run();

        Iterator<?> iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - GroupsTest", iterator.next());
        assertEquals("[Test]    testBeforeClass   - GroupsTest", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - GroupsTest", iterator.next());
        assertEquals("[Test]    testSetUp         - GroupsTest", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - GroupsTest", iterator.next());
        assertEquals("[Test]    testMethod        - GroupsTest", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - GroupsTest", iterator.next());
        assertEquals("[Test]    testTearDown      - GroupsTest", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - GroupsTest", iterator.next());
        assertEquals("[Test]    testAfterClass    - GroupsTest", iterator.next());
        assertEquals("[Unitils] afterTestClass    - GroupsTest", iterator.next());
        assertEquals("[Unitils] afterAll", iterator.next());
        assertEquals(0, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test that defines an unknown test group.
     */
    @Test
    public void testUnitilsTestNG_unknownGroup() {
        TestListenerAdapter testListenerAdapter = new TestListenerAdapter();

        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_GroupsTest.class});
        testng.setGroups("xxxx");
        testng.addListener(testListenerAdapter);
        testng.run();

        Iterator<?> iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] afterAll", iterator.next());
        assertEquals(0, testListenerAdapter.getFailedTests().size());
    }


    /**
     * Asserts that the given listener recorded the correct invocation sequence. Except for some minor difference, the
     * sequence should be equal for  all test frameworks.
     * <p/>
     * Following difference are allowed:<ul>
     * <li>beforeTestClass and afterTestClass no not exist in JUnit 3 (the Unitils versions will be called however)</li>
     * <li>empty tests are not run at all in JUnit3 and TestNG, in JUnit 4 the beforeTestClass and afterTestClass will be called</li>
     * </ul>
     * For JUnit3 and JUnit4 afterAll will be called during the runtime exit and can therefore not be asserted here.
     * The same is true for the last afterTestClass method of JUnit3 tests. This is because you cannot determine which test
     * is going to be the last test in the class
     *
     * @param type                JUnit3, JUnit4 or TestNG
     * @param tracingTestListener the listener, not null
     */
    private void assertInvocationOrder(String type, TracingTestListener tracingTestListener) {
        Iterator<?> iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    testBeforeClass   - TestClass1", iterator.next());
        }
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1", iterator.next());
        assertEquals("[Test]    testMethod        - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1", iterator.next());
        assertEquals("[Test]    testMethod        - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    testAfterClass    - TestClass1", iterator.next());
        }
        assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass2", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    testBeforeClass   - TestClass2", iterator.next());
        }
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2", iterator.next());
        assertEquals("[Test]    testMethod        - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2", iterator.next());
        assertEquals("[Test]    testMethod        - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    testAfterClass    - TestClass2", iterator.next());
            // last afterTestClass (TestClass2) will be called when the runtime exits
            assertEquals("[Unitils] afterTestClass    - TestClass2", iterator.next());
        }

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

        @Override
        protected Unitils getUnitils() {

            return new Unitils() {

                @Override
                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
    }


    /**
     * JUnit 3 test class without any tests. Inner class to avoid a failing test.
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
                    public TestListener createTestListener() {
                        return tracingTestListener;
                    }
                };
            }
            return super.getUnitils();
        }
    }
}
