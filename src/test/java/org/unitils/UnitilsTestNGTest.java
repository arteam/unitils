package org.unitils;

import static org.unitils.TracingTestListener.TestInvocation.TEST_AFTER_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_BEFORE_CLASS;
import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsTestNGTest extends UnitilsTestNG {

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


    @BeforeClass
    public void beforeClass() {
        registerTestInvocation(TEST_BEFORE_CLASS, null);
    }


    @AfterClass()
    public void afterClass() {
        registerTestInvocation(TEST_AFTER_CLASS, null);
    }


    @BeforeMethod
    public void setUp() {
        registerTestInvocation(TEST_SET_UP, null);
    }


    @AfterMethod
    public void tearDown() {
        registerTestInvocation(TEST_TEAR_DOWN, null);
    }
    
    
    /**
     * Records an invocation.
     *
     * @param invocation     the invocation type, not null
     * @param testMethodName the actual test name, null if not applicable
     */
    protected void registerTestInvocation(TracingTestListener.TestInvocation invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.registerTestInvocation(invocation, this.getClass(), testMethodName);
        }
    }
}
