package org.unitils;

import junit.framework.TestCase;
import org.junit.*;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test for {@link UnitilsJUnit4} and {@link UnitilsJUnit4TestClassRunner}.
 */
public class UnitilsJUnit4Test extends TestCase {


    /* List that will contain a string representation of each method call */
    private static List<String> callList = new ArrayList<String>();


    /**
     * Tests the correct invocation sequence of listener methods for a JUnit 4 test.
     * <p/>
     * 3 tests are performed: TestClass1 and TestClass2 both with 2 test methods and EmptyTestClass
     * that does not contain any methods.
     */
    public void testUnitilsJUnit4() throws Exception {

        TestUnitilsJUnit4TestClassRunner testRunner1 = new TestUnitilsJUnit4TestClassRunner(TestClass1.class);
        TestUnitilsJUnit4TestClassRunner testRunner2 = new TestUnitilsJUnit4TestClassRunner(TestClass2.class);
        TestUnitilsJUnit4TestClassRunner testRunner3 = new TestUnitilsJUnit4TestClassRunner(EmptyTestClass.class);
        testRunner1.run(new RunNotifier());
        testRunner2.run(new RunNotifier());
        testRunner3.run(new RunNotifier());

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[JUnit]   beforeTestClass   - TestClass1", iterator.next());
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
        assertEquals("[JUnit]   afterTestClass    - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass2", iterator.next());
        assertEquals("[JUnit]   beforeTestClass   - TestClass2", iterator.next());
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
        assertEquals("[JUnit]   afterTestClass    - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestClass    - TestClass2", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - EmptyTestClass", iterator.next());
        assertEquals("[Unitils] afterTestClass    - EmptyTestClass", iterator.next());

        // afterAll is called when the runtime exits
        assertFalse(iterator.hasNext());
    }


    /**
     * JUnit 4 test class containing 2 active and 1 ignored test method
     */
    public static class TestClass1 extends UnitilsJUnit4 {

        @BeforeClass
        public static void beforeClass() {
            callList.add("[JUnit]   beforeTestClass   - TestClass1");
        }

        @AfterClass
        public static void afterClass() {
            callList.add("[JUnit]   afterTestClass    - TestClass1");
        }

        @Before
        public void setUp() {
            callList.add("[JUnit]   testSetUp         - TestClass1");
        }

        @After
        public void tearDown() {
            callList.add("[JUnit]   testTearDown      - TestClass1");
        }

        @Test
        public void test1() {
            callList.add("[JUnit]   testMethod        - TestClass1 - test1");
        }

        @Test
        public void test2() {
            callList.add("[JUnit]   testMethod        - TestClass1 - test2");
        }

        @Ignore
        @Test
        public void test3() {
            callList.add("[JUnit]   testMethod        - TestClass1 - test2");
        }
    }


    /**
     * JUnit 4 test class containing 2 test methods
     */
    public static class TestClass2 extends UnitilsJUnit4 {

        @BeforeClass
        public static void beforeClass() {
            callList.add("[JUnit]   beforeTestClass   - TestClass2");
        }

        @AfterClass
        public static void afterClass() {
            callList.add("[JUnit]   afterTestClass    - TestClass2");
        }

        @Before
        public void setUp() {
            callList.add("[JUnit]   testSetUp         - TestClass2");
        }

        @After
        public void tearDown() {
            callList.add("[JUnit]   testTearDown      - TestClass2");
        }

        @Test
        public void test1() {
            callList.add("[JUnit]   testMethod        - TestClass2 - test1");
        }

        @Test
        public void test2() {
            callList.add("[JUnit]   testMethod        - TestClass2 - test2");
        }
    }


    /**
     * Empty JUnit 4 test class
     */
    public static class EmptyTestClass extends UnitilsJUnit4 {

    }


    /**
     * Overridden test class runner to be able to use the {@link TracingTestListener} as test listener.
     */
    private static class TestUnitilsJUnit4TestClassRunner extends UnitilsJUnit4TestClassRunner {

        public TestUnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        public TestListener createTestListener() {
            return new TracingTestListener();
        }
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
