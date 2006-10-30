package org.unitils;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.unitils.inject.util.InjectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test for {@link UnitilsJUnit3}.
 * <p/>
 * todo afterTestClass + afterAll explain
 */
public class UnitilsInvocationExceptionTest extends TestCase {


    //todo move call list to tracing test listener
    /* List that will contain a string representation of each method call */
    private List<String> callList;

    private TracingTestListener tracingTestListener;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        callList = new ArrayList<String>();
        tracingTestListener = new TracingTestListener(callList);

        // clear state so that beforeAll is called
        InjectionUtils injectionUtils = new InjectionUtils();
        injectionUtils.injectStatic(false, UnitilsJUnit3.class, "beforeAllCalled");
        //todo fix nullpointer
//        injectionUtils.injectStatic(null, UnitilsJUnit3.class, "lastTestClass");

        UnitilsJUnit3Test_TestClass1.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_TestClass2.setTracingTestListener(tracingTestListener);
        UnitilsJUnit3Test_EmptyTestClass.setTracingTestListener(tracingTestListener);

        UnitilsJUnit4Test_TestClass1.setCallList(callList);
        UnitilsJUnit4Test_TestClass2.setCallList(callList);

        UnitilsTestNGTest_TestClass1.setCallList(callList);
        UnitilsTestNGTest_TestClass2.setCallList(callList);
        UnitilsTestNGTest_EmptyTestClass.setCallList(callList);
    }


    /**
     * todo javadoc
     */
    public void testUnitilsBeforeAll_RuntimeException() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_ALL, false);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next()); // once for each test
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(0, result.failureCount());
        assertEquals(2, result.errorCount());
    }


    public void testUnitilsBeforeAll_AssertionFailedError() {

        tracingTestListener.setExceptionMethod(TracingTestListener.BEFORE_ALL, true);

        TestResult result = TestRunner.run(new TestSuite(UnitilsJUnit3Test_TestClass1.class));

        Iterator iterator = callList.iterator();
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

        Iterator iterator = callList.iterator();
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

        Iterator iterator = callList.iterator();
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

        Iterator iterator = callList.iterator();
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

        Iterator iterator = callList.iterator();
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

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
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

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
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

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
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

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
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

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
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

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());
        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass1 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertFalse(iterator.hasNext());

        assertEquals(2, result.failureCount());
        assertEquals(0, result.errorCount());
    }


}
