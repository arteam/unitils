package org.unitils;

import org.unitils.core.TestListener;

/**
 * Empty JUnit 3 test class
 */
public class UnitilsJUnit3Test_EmptyTestClass extends UnitilsJUnit3 {


    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    @Override
    protected TestListener createTestListener() {
        if (tracingTestListener != null) {
            return tracingTestListener;
        }
        return super.createTestListener();
    }
}
