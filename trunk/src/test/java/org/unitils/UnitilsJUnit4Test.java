package org.unitils;

import junit.framework.TestCase;
import org.junit.*;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * Test for {@link org.unitils.UnitilsJUnit4}.
 * <p/>
 * todo implement
 */
public class UnitilsJUnit4Test extends TestCase {


    private static Stack<String> callStack = new Stack<String>();


    public void testUnitilsJUnit4() throws Exception {

        TestUnitilsJUnit4TestClassRunner testRunner = new TestUnitilsJUnit4TestClassRunner(TestClass1.class);
        testRunner.run(new RunNotifier());

        for (String element : callStack) {
            System.out.println(element);
        }
    }


    public static class TestClass1 extends UnitilsJUnit4 {

        @BeforeClass
        public static void beforeClass() throws Exception {
            callStack.push("TestClass1.beforeClass");
        }

        @AfterClass
        public static void afterClass() throws Exception {
            callStack.push("TestClass1.afterClass");
        }

        @Before
        public void setUp() throws Exception {
            callStack.push("TestClass1.setUp");
        }

        @After
        public void tearDown() throws Exception {
            callStack.push("TestClass1.tearDown");
        }

        @Test
        public void test1() {
            callStack.push("TestClass1.test1");
        }

        @Test
        public void test2() {
            callStack.push("TestClass1.test2");
        }


    }


    private static class TestUnitilsJUnit4TestClassRunner extends UnitilsJUnit4TestClassRunner {

        public TestUnitilsJUnit4TestClassRunner(Class<?> testClass) throws InitializationError {
            super(testClass);
        }

        public TestListener createTestListener() {
            return new TracingTestListener();
        }
    }


    private static class TracingTestListener extends TestListener {

        public void beforeAll() {
            callStack.push("beforeAll");
        }

        public void beforeTestClass(Object testObject) {
            callStack.push("beforeTestClass - testObject: " + testObject);
        }

        public void beforeTestSetUp(Object testObject) {
            callStack.push("beforeTestSetUp - testObject: " + testObject);
        }

        public void beforeTestMethod(Object testObject, Method testMethod) {
            callStack.push("beforeTestMethod - testObject: " + testObject + ", testMethod: " + testMethod);
        }

        public void afterTestMethod(Object testObject, Method testMethod) {
            callStack.push("afterTestMethod - testObject: " + testObject + ", testMethod: " + testMethod);
        }

        public void afterTestTearDown(Object testObject) {
            callStack.push("afterTestTearDown - testObject: " + testObject);
        }

        public void afterTestClass(Object testObject) {
            callStack.push("afterTestClass - testObject: " + testObject);
        }

        public void afterAll() {
            callStack.push("afterAll");
        }
    }


}
