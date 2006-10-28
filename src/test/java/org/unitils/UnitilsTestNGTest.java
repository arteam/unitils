package org.unitils;

import junit.framework.TestCase;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test for {@link UnitilsTestNG}.
 */
public class UnitilsTestNGTest extends TestCase {


    /* List that will contain a string representation of each method call */
    public static List<String> callList = new ArrayList<String>();


    /**
     * Tests the correct invocation sequence of listener methods for a TestNG test.
     * <p/>
     * 3 tests are performed: UnitilsTestNGTest_TestClass1 and UnitilsTestNGTest_TestClass2 both with 2 test methods
     * and UnitilsTestNGTest_EmptyTestClass that does not contain any methods.
     */
    public void testUnitilsTestNG() {

        TestNG testng = new TestNG();
        testng.setTestClasses(new Class[]{UnitilsTestNGTest_TestClass1.class, UnitilsTestNGTest_TestClass2.class, UnitilsTestNGTest_EmptyTestClass.class});
        testng.addListener(new TestListenerAdapter());
        testng.run();

        Iterator iterator = callList.iterator();
        assertEquals("[Unitils] beforeAll", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[TestNG]  beforeTestClass   - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[TestNG]  testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test1", iterator.next());
        assertEquals("[TestNG]  testMethod        - TestClass1 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test1", iterator.next());
        assertEquals("[TestNG]  testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass1", iterator.next());
        assertEquals("[TestNG]  testSetUp         - TestClass1", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass1 - test2", iterator.next());
        assertEquals("[TestNG]  testMethod        - TestClass1 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass1 - test2", iterator.next());
        assertEquals("[TestNG]  testTearDown      - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass1", iterator.next());
        assertEquals("[TestNG]  afterTestClass    - TestClass1", iterator.next());
        assertEquals("[Unitils] afterTestClass    - TestClass1", iterator.next());

        assertEquals("[Unitils] beforeTestClass   - TestClass2", iterator.next());
        assertEquals("[TestNG]  beforeTestClass   - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[TestNG]  testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2 - test1", iterator.next());
        assertEquals("[TestNG]  testMethod        - TestClass2 - test1", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2 - test1", iterator.next());
        assertEquals("[TestNG]  testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestSetUp   - TestClass2", iterator.next());
        assertEquals("[TestNG]  testSetUp         - TestClass2", iterator.next());
        assertEquals("[Unitils] beforeTestMethod  - TestClass2 - test2", iterator.next());
        assertEquals("[TestNG]  testMethod        - TestClass2 - test2", iterator.next());
        assertEquals("[Unitils] afterTestMethod   - TestClass2 - test2", iterator.next());
        assertEquals("[TestNG]  testTearDown      - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestTearDown - TestClass2", iterator.next());
        assertEquals("[TestNG]  afterTestClass    - TestClass2", iterator.next());
        assertEquals("[Unitils] afterTestClass    - TestClass2", iterator.next());

        // EmptyTestClass has no tests and will not be run
        assertEquals("[Unitils] afterAll", iterator.next());

        assertFalse(iterator.hasNext());
    }


    /**
     * Test listener that records all method invocations.
     */
    public static class TracingTestListener extends TestListener {

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
            callList.add("[Unitils] afterAll");
        }

        private String getClassName(Object object) {
            String className = (object instanceof Class) ? ((Class) object).getName() : object.getClass().getName();
            return className.substring(className.lastIndexOf('_') + 1);
        }
    }

}
