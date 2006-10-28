package org.unitils;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test for {@link UnitilsJUnit3}.
 */
public class UnitilsJUnit3Test extends TestCase {


    /* List that will contain a string representation of each method call */
    private static List<String> callList = new ArrayList<String>();

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
    public void testUnitilsJUnit3() {

        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestClass1.class);
        suite.addTestSuite(TestClass2.class);
        suite.addTestSuite(EmptyTestClass.class);

        TestRunner testRunner = new TestRunner();
        testRunner.doRun(suite);

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
        assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2 - test1", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass2 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2 - test1", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[JUnit]   testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2 - test2", iterator.next());
        assertEquals("[JUnit]   testMethod        - TestClass2 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2 - test2", iterator.next());
        assertEquals("[JUnit]   testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());

        // EmptyTestClass has caused a failure and will not be run

        // afterTestClass TestClass2 is called when the runtime exits
        // afterAll is called when the runtime exits
        assertFalse(iterator.hasNext());
    }


    /**
     * JUnit 3 test class containing 2 test methods
     */
    public static class TestClass1 extends UnitilsJUnit3 {


        protected void setUp() throws Exception {
            super.setUp();
            callList.add("[JUnit]   testSetUp         - TestClass1");
        }

        protected void tearDown() throws Exception {
            super.tearDown();
            callList.add("[JUnit]   testTearDown      - TestClass1");
        }

        public void test1() {
            callList.add("[JUnit]   testMethod        - TestClass1 - test1");
        }

        public void test2() {
            callList.add("[JUnit]   testMethod        - TestClass1 - test2");
        }


        protected TestListener createTestListener() {
            return new TracingTestListener();
        }
    }


    /**
     * JUnit 3 test class containing 2 test methods
     */
    public static class TestClass2 extends UnitilsJUnit3 {


        protected void setUp() throws Exception {
            super.setUp();
            callList.add("[JUnit]   testSetUp         - TestClass2");
        }

        protected void tearDown() throws Exception {
            super.tearDown();
            callList.add("[JUnit]   testTearDown      - TestClass2");
        }

        public void test1() {
            callList.add("[JUnit]   testMethod        - TestClass2 - test1");
        }

        public void test2() {
            callList.add("[JUnit]   testMethod        - TestClass2 - test2");
        }

        protected TestListener createTestListener() {
            return new TracingTestListener();
        }
    }


    /**
     * Empty JUnit 3 test class
     */
    public static class EmptyTestClass extends UnitilsJUnit3 {

    }


    /**
     * Test listener that records all method invocations.
     */
    private static class TracingTestListener extends TestListener {

        @Override
        public void beforeAll() {
            callList.add("[Unitils] beforeAll");
        }

        @Override
        public void beforeTestClass(Class testClass) {
            callList.add("[Unitils] beforeTestClass   - " + getClassName(testClass));
        }

        @Override
        public void beforeTestSetUp(Object testObject) {
            callList.add("[Unitils] beforeTestSetUp   - " + getClassName(testObject));
        }

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            callList.add("[Unitils] beforeTestMethod  - " + getClassName(testObject) + " - " + testMethod.getName());
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod) {
            callList.add("[Unitils] afterTestMethod   - " + getClassName(testObject) + " - " + testMethod.getName());
        }

        @Override
        public void afterTestTearDown(Object testObject) {
            callList.add("[Unitils] afterTestTearDown - " + getClassName(testObject));
        }

        @Override
        public void afterTestClass(Class testClass) {
            callList.add("[Unitils] afterTestClass    - " + getClassName(testClass));
            // last one is called during runtime exit
        }

        @Override
        public void afterAll() {
            // called during Runtime exit
        }

        private String getClassName(Object object) {
            String className = (object instanceof Class) ? ((Class) object).getName() : object.getClass().getName();
            return className.substring(className.lastIndexOf('$') + 1);
        }
    }
}
