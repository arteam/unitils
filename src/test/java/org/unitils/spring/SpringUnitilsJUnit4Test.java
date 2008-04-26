package org.unitils.spring;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.unitils.TracingTestListener;
import org.unitils.spring.util.SpringUnitilsAdaptorTestExecutionListener;


// todo javadoc
@TestExecutionListeners(value = SpringUnitilsAdaptorTestExecutionListener.class, inheritListeners = false)
abstract public class SpringUnitilsJUnit4Test extends AbstractJUnit4SpringContextTests {

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
