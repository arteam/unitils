package org.unitils;


abstract public class UnitilsJUnit4Test extends UnitilsJUnit4 {

	/* Test listener that will record all invocations */
    private static TracingTestListener tracingTestListener;


    /**
     * Sets the tracing test listener that will record all invocations.
     *
     * @param testListener the listener
     */
    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }

    /**
     * Records an invocation.
     *
     * @param invocation     the invocation type, not null
     * @param test           the test instance or class, not null
     * @param testMethodName the actual test name, null if not applicable
     */
    protected static void registerTestInvocation(TracingTestListener.TestInvocation invocation, Class<?> testClass, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.registerTestInvocation(invocation, testClass, testMethodName);
        }
    }

}
