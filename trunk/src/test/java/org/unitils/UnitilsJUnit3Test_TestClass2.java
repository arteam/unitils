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
        tracingTestListener.addTestInvocation("testSetUp", this, null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        tracingTestListener.addTestInvocation("testTearDown", this, null);
    }

    public void test1() {
        tracingTestListener.addTestInvocation("testMethod", this, "test1");
    }

    public void test2() {
        tracingTestListener.addTestInvocation("testMethod", this, "test2");
    }


    @Override
    protected TestListener createTestListener() {
        if (tracingTestListener != null) {
            return tracingTestListener;
        }
        return super.createTestListener();
    }
}

