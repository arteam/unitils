package org.unitils;

import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

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
        addTestInvocation("testSetUp", null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        addTestInvocation("testTearDown", null);
    }

    public void test1() {
        addTestInvocation("testMethod", "test1");
    }

    public void test2() {
        addTestInvocation("testMethod", "test2");
    }

    private void addTestInvocation(String invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.addTestInvocation(invocation, this, testMethodName);
        }
    }

    @Override
    protected Unitils getUnitils() {
        if (tracingTestListener != null) {
            return new Unitils() {

                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
        return super.getUnitils();
    }
}

