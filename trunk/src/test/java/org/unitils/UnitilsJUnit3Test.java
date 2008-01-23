package org.unitils;

import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;

abstract public class UnitilsJUnit3Test extends UnitilsJUnit3 {

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


    protected void setUp() throws Exception {
        super.setUp();
        registerTestInvocation(TEST_SET_UP, null);
    }


    protected void tearDown() throws Exception {
        super.tearDown();
        registerTestInvocation(TEST_TEAR_DOWN, null);
    }

    
    /**
     * Records an invocation.
     *
     * @param invocation     the invocation type, not null
     * @param testMethod TODO
     * @param testMethodName the actual test name, null if not applicable
     */
    protected void registerTestInvocation(TracingTestListener.TestInvocation invocation, String testMethod) {
        if (tracingTestListener != null) {
			tracingTestListener.registerTestInvocation(invocation, this
					.getClass(), testMethod);
		}
    }

}
