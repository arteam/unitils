package org.unitils;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.unitils.inject.util.InjectionUtils;

import java.util.Iterator;

/**
 * Test for {@link UnitilsJUnit3}.
 * <p/>
 * todo afterTestClass + afterAll explain
 */
public class UnitilsInvocationExceptionTest extends TestCase {


    /* Listener that records all test method invocations */
    private TracingTestListener tracingTestListener;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tracingTestListener = new TracingTestListener();

        // clear state so that beforeAll is called
        InjectionUtils.injectStatic(false, UnitilsJUnit3.class, "beforeAllCalled");
        InjectionUtils.injectStatic(null, UnitilsJUnit3.class, "lastTestClass");
        InjectionUtils.injectStatic(null, UnitilsJUnit4TestClassRunner.class, "testListener");

        UnitilsJUnit3Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(tracingTestListener);

        UnitilsJUnit4Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit4Test_TestClass2.setTracingTestListener(tracingTestListener);

        UnitilsTestNGTest_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsTestNGTest_EmptyTestClass.setTracingTestListener(tracingTestListener);
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeAll_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_ALL, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next()); // once for each test
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    public void testUnitilsBeforeAll_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_ALL, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next()); // once for each test
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeTestClass_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_TEST_CLASS, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeTestClass_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_TEST_CLASS, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeTestSetUp_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_TEST_SET_UP, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeTestSetUp_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_TEST_SET_UP, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_TEST_METHOD, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());
        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_TEST_METHOD, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[Test]    testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[Test]    testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsAfterTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.AFTER_TEST_METHOD, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

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
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsAfterTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.AFTER_TEST_METHOD, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

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
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsAfterTestTearDown_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.AFTER_TEST_TEAR_DOWN, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

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
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    /**
     * todo javadoc
     */
    public void testUnitilsAfterTestTearDown_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.AFTER_TEST_TEAR_DOWN, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

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
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


}
