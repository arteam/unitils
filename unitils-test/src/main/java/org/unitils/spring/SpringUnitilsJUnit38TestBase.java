package org.unitils.spring;

import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.unitils.TracingTestListener;
import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;
import org.unitils.spring.util.SpringUnitilsAdaptorTestExecutionListener;

// todo javadoc
@TestExecutionListeners(value = SpringUnitilsAdaptorTestExecutionListener.class, inheritListeners = false)
abstract public class SpringUnitilsJUnit38TestBase extends AbstractJUnit38SpringContextTests {

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
     * @param testMethodName the test method, null if unknown or not applicable
     */
    protected void registerTestInvocation(TracingTestListener.TestInvocation invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.registerTestInvocation(invocation, this
                    .getClass(), testMethodName);
        }
    }

}
