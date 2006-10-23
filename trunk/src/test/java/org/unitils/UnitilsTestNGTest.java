package org.unitils;

import junit.framework.TestCase;
import org.testng.annotations.*;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * Test for {@link org.unitils.UnitilsJUnit3}.
 * <p/>
 * todo implement
 */
public class UnitilsTestNGTest extends TestCase {


    private Stack<String> callStack = new Stack<String>();


    public void testUnitilsJUnit3() {


        for (String element : callStack) {
            System.out.println(element);
        }
    }


    public class TestClass1 extends UnitilsTestNG {


        @BeforeClass
        protected void beforeClass() throws Exception {
            callStack.push("TestClass1.beforeClass");
        }

        @BeforeTest
        protected void beforeTest() throws Exception {
            callStack.push("TestClass1.beforeTest");
        }

        @Test
        public void test1() {
            callStack.push("TestClass1.test1");
        }

        @Test
        public void test2() {
            callStack.push("TestClass1.test2");
        }

        @AfterTest
        protected void afterTest() throws Exception {
            callStack.push("TestClass1.afterTest");
        }

        @AfterClass
        protected void afterClass() throws Exception {
            callStack.push("TestClass1.afterClass");
        }

        protected TestListener createTestListener() {
            return new TracingTestListener();
        }
    }


    private class TracingTestListener extends TestListener {

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
