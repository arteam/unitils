package org.unitils;

import junit.framework.AssertionFailedError;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Test listener that records all method invocations.
 */
public class TracingTestListener extends TestListener {

    public static final String BEFORE_ALL = "[Unitils] beforeAll";
    public static final String BEFORE_TEST_CLASS = "[Unitils] beforeTestClass";
    public static final String BEFORE_TEST_SET_UP = "[Unitils] beforeTestSetUp";
    public static final String BEFORE_TEST_METHOD = "[Unitils] beforeTestMethod";
    public static final String AFTER_TEST_METHOD = "[Unitils] afterTestMethod";
    public static final String AFTER_TEST_TEAR_DOWN = "[Unitils] afterTestTearDown";
    public static final String AFTER_TEST_CLASS = "[Unitils] afterTestClass";
    public static final String AFTER_ALL = "[Unitils] afterAll";


    /* List that will contain a string representation of each method call */
    private List<String> callList;

    private String exceptionMethod;

    private boolean throwAssertionFailedError;


    public TracingTestListener(List<String> callList) {
        this.callList = callList;
    }


    public void setExceptionMethod(String exceptionMethod, boolean throwAssertionFailedError) {
        this.exceptionMethod = exceptionMethod;
        this.throwAssertionFailedError = throwAssertionFailedError;
    }


    public void addTestInvocation(String invocation) {
        callList.add(invocation);
    }


    @Override
    public void beforeAll() {
        callList.add(BEFORE_ALL);
        throwExceptionIfRequested(BEFORE_ALL);
    }

    @Override
    public void beforeTestClass(Class testClass) {
        callList.add(BEFORE_TEST_CLASS + "   - " + getClassName(testClass));
        throwExceptionIfRequested(BEFORE_TEST_CLASS);
    }

    @Override
    public void beforeTestSetUp(Object testObject) {
        callList.add(BEFORE_TEST_SET_UP + "   - " + getClassName(testObject));
        throwExceptionIfRequested(BEFORE_TEST_SET_UP);
    }

    @Override
    public void beforeTestMethod(Object testObject, Method testMethod) {
        callList.add(BEFORE_TEST_METHOD + "  - " + getClassName(testObject) + " - " + testMethod.getName());
        throwExceptionIfRequested(BEFORE_TEST_METHOD);
    }

    @Override
    public void afterTestMethod(Object testObject, Method testMethod) {
        callList.add(AFTER_TEST_METHOD + "   - " + getClassName(testObject) + " - " + testMethod.getName());
        throwExceptionIfRequested(AFTER_TEST_METHOD);
    }

    @Override
    public void afterTestTearDown(Object testObject) {
        callList.add(AFTER_TEST_TEAR_DOWN + " - " + getClassName(testObject));
        throwExceptionIfRequested(AFTER_TEST_TEAR_DOWN);
    }

    @Override
    public void afterTestClass(Class testClass) {
        callList.add(AFTER_TEST_CLASS + "   - " + getClassName(testClass));
        throwExceptionIfRequested(AFTER_TEST_CLASS);
    }

    @Override
    public void afterAll() {
        callList.add(AFTER_ALL);
        throwExceptionIfRequested(AFTER_ALL);
    }


    private String getClassName(Object object) {
        String className = (object instanceof Class) ? ((Class) object).getName() : object.getClass().getName();
        return className.substring(className.lastIndexOf('_') + 1);
    }


    private void throwExceptionIfRequested(String message) {
        if (exceptionMethod == null || !exceptionMethod.equals(message)) {
            return;
        }
        if (throwAssertionFailedError) {
            throw new AssertionFailedError(message);
        }
        throw new RuntimeException(message);
    }
}


