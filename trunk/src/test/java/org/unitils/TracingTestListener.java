/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils;

import junit.framework.AssertionFailedError;
import org.apache.commons.lang.StringUtils;
import org.unitils.core.TestListener;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Test listener that records all method invocations.
 */
public class TracingTestListener extends TestListener {

    public static final String BEFORE_ALL = "beforeAll";
    public static final String BEFORE_TEST_CLASS = "beforeTestClass";
    public static final String BEFORE_TEST_SET_UP = "beforeTestSetUp";
    public static final String BEFORE_TEST_METHOD = "beforeTestMethod";
    public static final String AFTER_TEST_METHOD = "afterTestMethod";
    public static final String AFTER_TEST_TEAR_DOWN = "afterTestTearDown";
    public static final String AFTER_TEST_CLASS = "afterTestClass";
    public static final String AFTER_ALL = "afterAll";

    public static final String TEST_BEFORE_CLASS = "testBeforeClass";
    public static final String TEST_SET_UP = "testSetUp";
    public static final String TEST_METHOD = "testMethod";
    public static final String TEST_TEAR_DOWN = "testTearDown";
    public static final String TEST_AFTER_CLASS = "testAfterClass";

    private static final String TEST = "[Test]";
    private static final String UNITILS = "[Unitils]";


    /* List that will contain a string representation of each method call */
    private List<String> callList;

    private String exceptionMethod;

    private boolean throwAssertionFailedError;


    public TracingTestListener() {
        this.callList = new ArrayList<String>();
    }


    public List<String> getCallList() {
        return callList;
    }

    public void setExceptionMethod(String exceptionMethod, boolean throwAssertionFailedError) {
        this.exceptionMethod = exceptionMethod;
        this.throwAssertionFailedError = throwAssertionFailedError;
    }


    public void addTestInvocation(String invocation, Object test, String testMethodName) {
        callList.add(formatString(TEST, invocation, getClassName(test), testMethodName));
        throwExceptionIfRequested(testMethodName);
    }


    @Override
    public void beforeAll() {
        callList.add(formatString(UNITILS, BEFORE_ALL, null, null));
        throwExceptionIfRequested(BEFORE_ALL);
    }

    @Override
    public void beforeTestClass(Class testClass) {
        callList.add(formatString(UNITILS, BEFORE_TEST_CLASS, getClassName(testClass), null));
        throwExceptionIfRequested(BEFORE_TEST_CLASS);
    }

    @Override
    public void beforeTestSetUp(Object testObject) {
        callList.add(formatString(UNITILS, BEFORE_TEST_SET_UP, getClassName(testObject), null));
        throwExceptionIfRequested(BEFORE_TEST_SET_UP);
    }

    @Override
    public void beforeTestMethod(Object testObject, Method testMethod) {
        callList.add(formatString(UNITILS, BEFORE_TEST_METHOD, getClassName(testObject), testMethod.getName()));
        throwExceptionIfRequested(BEFORE_TEST_METHOD);
    }

    @Override
    public void afterTestMethod(Object testObject, Method testMethod) {
        callList.add(formatString(UNITILS, AFTER_TEST_METHOD, getClassName(testObject), testMethod.getName()));
        throwExceptionIfRequested(AFTER_TEST_METHOD);
    }

    @Override
    public void afterTestTearDown(Object testObject) {
        callList.add(formatString(UNITILS, AFTER_TEST_TEAR_DOWN, getClassName(testObject), null));
        throwExceptionIfRequested(AFTER_TEST_TEAR_DOWN);
    }

    @Override
    public void afterTestClass(Class testClass) {
        callList.add(formatString(UNITILS, AFTER_TEST_CLASS, getClassName(testClass), null));
        throwExceptionIfRequested(AFTER_TEST_CLASS);
    }

    @Override
    public void afterAll() {
        callList.add(formatString(UNITILS, AFTER_ALL, null, null));
        throwExceptionIfRequested(AFTER_ALL);
    }


    private String getClassName(Object object) {
        if (object == null) {
            return null;
        }
        String className = (object instanceof Class) ? ((Class) object).getName() : object.getClass().getName();
        return className.substring(className.lastIndexOf('_') + 1);
    }


    private void throwExceptionIfRequested(String exceptionMethod) {
        if (this.exceptionMethod == null || !this.exceptionMethod.equals(exceptionMethod)) {
            return;
        }
        if (throwAssertionFailedError) {
            throw new AssertionFailedError(exceptionMethod);
        }
        throw new RuntimeException(exceptionMethod);
    }


    private String formatString(String type, String invocation, String testClass, String testMethodName) {

        String result = StringUtils.rightPad(type, 10);
        result += StringUtils.rightPad(invocation, 17);
        if (!StringUtils.isEmpty(testClass)) {
            result += " - " + StringUtils.rightPad(testClass, 10);
            if (!StringUtils.isEmpty(testMethodName)) {
                result += " - " + testMethodName;
            }
        }
        return result.trim();
    }
}



