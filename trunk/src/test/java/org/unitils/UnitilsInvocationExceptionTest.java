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
import static org.unitils.TracingTestListener.*;
import org.unitils.core.TestListener;
import org.unitils.inject.util.InjectionUtils;
import org.unitils.util.ReflectionUtils;

import java.util.Iterator;

/**
 * Test for {@link UnitilsJUnit3}.
 * <p/>
 * todo afterTestClass + afterAll explain
 */
public class UnitilsInvocationExceptionTest {


    /* Listener that records all test method invocations */
    private static TracingTestListener tracingTestListener;

    private static TestListener oldTestListenerUnitilsJUnit3;

    private static TestListener oldTestListenerUnitilsJUnit4;


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
     * todo javadoc
     */
    @Test
    public void testUnitilsBeforeAll_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = tracingTestListener.getCallList().iterator();
        assertEquals("[Unitils] beforeAll", iterator.next()); // once for each test
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    @Test
    public void testUnitilsBeforeAll_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_ALL, true);

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
    @Test
    public void testUnitilsBeforeTestClass_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, false);

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
    @Test
    public void testUnitilsBeforeTestClass_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_CLASS, true);

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
    @Test
    public void testUnitilsBeforeTestSetUp_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, false);

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
    @Test
    public void testUnitilsBeforeTestSetUp_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_SET_UP, true);

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
    @Test
    public void testUnitilsBeforeTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, false);

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
    @Test
    public void testUnitilsBeforeTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(BEFORE_TEST_METHOD, true);

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
    @Test
    public void testUnitilsAfterTestMethod_RuntimeException() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, false);

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
    @Test
    public void testUnitilsAfterTestMethod_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_METHOD, true);

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
    @Test
    public void testUnitilsAfterTestTearDown_RuntimeException() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, false);

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
    @Test
    public void testUnitilsAfterTestTearDown_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(AFTER_TEST_TEAR_DOWN, true);

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
