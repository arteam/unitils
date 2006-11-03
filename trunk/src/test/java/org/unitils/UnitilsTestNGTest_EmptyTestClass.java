package org.unitils;

import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * Empty TestNG test class
 * <p/>
 * Test class used in the {@link UnitilsInvocationTest} tests.
 * This is a public class because there is a bug in TestNG that does not allow tests on inner classes.
 */
public class UnitilsTestNGTest_EmptyTestClass extends UnitilsTestNG {


    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
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
