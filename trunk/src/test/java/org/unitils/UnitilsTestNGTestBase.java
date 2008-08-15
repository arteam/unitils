/*
 * Copyright 2008,  Unitils.org
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

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import static org.unitils.TracingTestListener.TestInvocation.*;

/**
 * Base class for the TestNG test listener tests.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class UnitilsTestNGTestBase extends UnitilsTestNG {

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
     * Register the test before class invocation.
     */
    @BeforeClass
    public void beforeClass() {
        registerTestInvocation(TEST_BEFORE_CLASS, null);
    }


    /**
     * Register the test after class invocation.
     */
    @AfterClass()
    public void afterClass() {
        registerTestInvocation(TEST_AFTER_CLASS, null);
    }


    /**
     * Register the test test setup invocation.
     */
    @BeforeMethod
    public void setUp() {
        registerTestInvocation(TEST_SET_UP, null);
    }


    /**
     * Register the test tear down invocation.
     */
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
            tracingTestListener.registerTestInvocation(invocation, getClass(), testMethodName);
        }
    }
}
