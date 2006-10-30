package org.unitils;

import org.unitils.core.TestListener;

/**
 * JUnit 3 test class containing 2 test methods
 */
public class UnitilsJUnit3Test_TestClass2 extends UnitilsJUnit3 {

    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    protected void setUp() throws Exception {
        super.setUp();
        tracingTestListener.addTestInvocation("[Test]    testSetUp         - TestClass2");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        tracingTestListener.addTestInvocation("[Test]    testTearDown      - TestClass2");
    }

    public void test1() {
        tracingTestListener.addTestInvocation("[Test]    testMethod        - TestClass2 - test1");
    }

    public void test2() {
        tracingTestListener.addTestInvocation("[Test]    testMethod        - TestClass2 - test2");
    }


    @Override
    protected TestListener createTestListener() {
        return tracingTestListener;
    }
}

