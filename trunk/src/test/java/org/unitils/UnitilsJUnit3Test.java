package org.unitils;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.Stack;

/**
 * Test for {@link UnitilsJUnit3}.
 * <p/>
 * todo implement
 */
public class UnitilsJUnit3Test extends TestCase {


    private Stack<String> callStack = new Stack<String>();


    public void testUnitilsJUnit3() {

        TestSuite suite = new TestSuite();
        suite.addTest(new TestClass1("test1"));
        suite.addTest(new TestClass1("test2"));
        suite.addTest(new TestClass2("test1"));
        suite.addTest(new TestClass2("test2"));

        TestRunner testRunner = new TestRunner();
        testRunner.doRun(suite);

        for (String element : callStack) {
            System.out.println(element);
        }
    }


    public class TestClass1 extends UnitilsJUnit3 {

        public TestClass1(String name) {
            super(name);
        }

        protected void setUp() throws Exception {
            super.setUp();
            callStack.push("TestClass1.setUp");
        }

        protected void tearDown() throws Exception {
            super.tearDown();
            callStack.push("TestClass1.tearDown");
        }


        public void test1() {
            callStack.push("TestClass1.test1");
        }

        public void test2() {
            callStack.push("TestClass1.test2");
        }

        protected TestListener createTestListener() {
            return new TracingTestListener("TestClass1");
        }
    }


    public class TestClass2 extends UnitilsJUnit3 {

        public TestClass2(String name) {
            super(name);
        }

        protected void setUp() throws Exception {
            super.setUp();
            callStack.push("TestClass2.setUp");
        }

        protected void tearDown() throws Exception {
            super.tearDown();
            callStack.push("TestClass2.tearDown");
        }

        public void test1() {
            callStack.push("TestClass2.test1");
        }

        public void test2() {
            callStack.push("TestClass2.test2");
        }

        protected TestListener createTestListener() {
            return new TracingTestListener("TestClass2");
        }
    }


    private class TracingTestListener extends TestListener {

        private String testName;

        public TracingTestListener(String testName) {
            this.testName = testName;
        }

        public void beforeAll() {
            callStack.push(testName + ".beforeAll");
        }

        public void beforeTestClass(Object testObject) {
            callStack.push(testName + ".beforeTestClass");
        }

        public void beforeTestSetUp(Object testObject) {
            callStack.push(testName + ".beforeTestSetUp");
        }

        public void beforeTestMethod(Object testObject, Method testMethod) {
            callStack.push(testName + ".beforeTestMethod");
        }

        public void afterTestMethod(Object testObject, Method testMethod) {
            callStack.push(testName + ".afterTestMethod");
        }

        public void afterTestTearDown(Object testObject) {
            callStack.push(testName + ".afterTestTearDown");
        }

        public void afterTestClass(Object testObject) {
            callStack.push(testName + ".afterTestClass");
        }

        public void afterAll() {
            callStack.push(testName + ".afterAll");
        }
    }

    ;


}
