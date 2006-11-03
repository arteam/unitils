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
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.util.ReflectionUtils;

import java.util.Iterator;

/**
 * todo javadoc
 * <p/>
 * Test for {@link UnitilsJUnit4} and {@link UnitilsJUnit4TestClassRunner}.
 */
public class UnitilsInvocationTest {

    /* Listener that records all test method invocations */
    private static TracingTestListener tracingTestListener;

    private static TestListener oldTestListenerUnitilsJUnit3;

    private static TestListener oldTestListenerUnitilsJUnit4;


    @BeforeClass
    public static void classSetup() {
        oldTestListenerUnitilsJUnit3 = (TestListener) ReflectionUtils.getFieldValue(null, ReflectionUtils.getFieldWithName(UnitilsJUnit3.class, "testListener", true));
        oldTestListenerUnitilsJUnit4 = (TestListener) ReflectionUtils.getFieldValue(null, ReflectionUtils.getFieldWithName(UnitilsJUnit4TestClassRunner.class, "testListener", true));

        InjectionUtils.injectStatic(null, UnitilsJUnit3.class, "testListener");
        InjectionUtils.injectStatic(null, UnitilsJUnit4TestClassRunner.class, "testListener");

        tracingTestListener = new TracingTestListener();

        UnitilsJUnit3Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(tracingTestListener);

        UnitilsJUnit4Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit4Test_TestClass2.setTracingTestListener(tracingTestListener);

        UnitilsTestNGTest_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(tracingTestListener);
    }


    @AfterClass
    public static void classTearDown() {

        InjectionUtils.injectStatic(oldTestListenerUnitilsJUnit3, UnitilsJUnit3.class, "testListener");
        InjectionUtils.injectStatic(oldTestListenerUnitilsJUnit4, UnitilsJUnit4TestClassRunner.class, "testListener");
    }

    @Before
    public void setUp() throws Exception {

        tracingTestListener.getCallList().clear();

        // clear state so that beforeAll is called
        InjectionUtils.injectStatic(false, UnitilsJUnit3.class, "beforeAllCalled");
        InjectionUtils.injectStatic(null, UnitilsJUnit3.class, "lastTestClass");
        InjectionUtils.injectStatic(false, UnitilsJUnit4TestClassRunner.class, "beforeAllCalled");

    }


    /**
     * Tests the correct invocation sequence of listener methods for a JUnit 3 test.
     * <p/>
     * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
     * that does not contain any methods.
     * <p/>
     * NOTE: there is a difference between JUnit 3 and JUnit 4 testruns:
     * beforeTestClass and afterTestClass no not exist in JUnit 3 (the Unitils versions will be called however)
     * empty tests are not run in JUnit 3, in JUnit 4 the before and after will be called
     * the last afterTestClass method will be run during the runtime exit. This is because we cannot determine which test
     * is going to be the last test in the class
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
     * Tests the correct invocation sequence of listener methods for a JUnit 4 test.
     * <p/>
     * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
     * that does not contain any methods.
     */
    @Test
    public void testUnitilsJUnit4() throws Exception {

        FailureRunListener failureRunListener = new FailureRunListener();
        RunNotifier runNotifier = new RunNotifier();
        runNotifier.addListener(failureRunListener);

        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass1.class);
        TestUnitilsJUnit4TestClassRunner testRunner2 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_TestClass2.class);
        TestUnitilsJUnit4TestClassRunner testRunner3 = new TestUnitilsJUnit4TestClassRunner(UnitilsJUnit4Test_EmptyTestClass.class);
        testRunner1.run(runNotifier);
        testRunner2.run(runNotifier);
        testRunner3.run(runNotifier);

        assertInvocationOrder("JUnit4", tracingTestListener);

        // EmptyTestClass has caused a failure
        assertEquals(1, failureRunListener.getFailureCount());
    }


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test.
     * <p/>
     * 3 tests are performed: UnitilsTestNGTest_TestClass1 and UnitilsTestNGTest_TestClass2 both with 2 test methods
     * and UnitilsTestNGTest_EmptyTestClass that does not contain any methods.
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


    // todo javadoc
    private void assertInvocationOrder(String type, TracingTestListener tracingTestListener) {
        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    beforeTestClass   - TestClass1", iterator.next());
        }
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
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    afterTestClass    - TestClass1", iterator.next());
        }
        assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass2", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    beforeTestClass   - TestClass2", iterator.next());
        }
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2 - test1", iterator.next());
        assertEquals("[Test]    testMethod        - TestClass2 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2 - test1", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2 - test2", iterator.next());
        assertEquals("[Test]    testMethod        - TestClass2 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2 - test2", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        if (!"JUnit3".equals(type)) {
            assertEquals("[Test]    afterTestClass    - TestClass2", iterator.next());
            // last afterTestClass (TestClass2) will be called when the runtime exits
            assertEquals("[Unitils] afterTestClass    - TestClass2", iterator.next());
        }

        // EmptyTestClass has no tests and will not be run by TestNG and JUnit 3
        if ("JUnit4".equals(type)) {
            assertEquals("[Unitils] beforeTestClass   - EmptyTestClass", iterator.next());
            assertEquals("[Unitils] afterTestClass    - EmptyTestClass", iterator.next());
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

        protected Unitils getUnitils() {

            return new Unitils() {

                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
    }


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


    /**
     * Empty JUnit 3 test class
     */
    private static class UnitilsJUnit3Test_EmptyTestClass extends UnitilsJUnit3 {


        private static TracingTestListener tracingTestListener;

        public static void setTracingTestListener(TracingTestListener testListener) {
            tracingTestListener = testListener;
        }


        @Override
        protected Unitils getUnitils() {
            if (tracingTestListener != null) {
                return new Unitils() {

                    public TestListener createTestListener() {
                        return tracingTestListener;
                    }
                };
            }
            return super.getUnitils();
        }
    }

}
