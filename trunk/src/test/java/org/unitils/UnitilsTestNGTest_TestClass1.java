/*
 * Copyright 2006-2007,  Unitils.org
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

import org.testng.annotations.*;
import static org.unitils.TracingTestListener.*;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * TestNG test class containing 2 active and 1 ignored test method. This test test-class is used
 * in the {@link UnitilsInvocationTest} and {@link UnitilsInvocationExceptionTest} tests.
 *
 * @author Tim Ducheyne
 */
public class UnitilsTestNGTest_TestClass1 extends UnitilsTestNG {


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
        addTestInvocation(TEST_BEFORE_CLASS, null);
    }


    @AfterClass
    public void afterClass() {
        addTestInvocation(TEST_AFTER_CLASS, null);
    }


    @BeforeMethod
    public void setUp() {
        addTestInvocation(TEST_SET_UP, null);
    }


    @AfterMethod
    public void tearDown() {
        addTestInvocation(TEST_TEAR_DOWN, null);
    }


    @Test
    public void test1() {
        addTestInvocation(TEST_METHOD, "test1");
    }


    @Test
    public void test2() {
        addTestInvocation(TEST_METHOD, "test2");
    }


    @Test(enabled = false)
    public void test3() {
        addTestInvocation(TEST_METHOD, "test3");
    }


    /**
     * Records an invocation.
     *
     * @param invocation     the invocation type, not null
     * @param testMethodName the actual test name, null if not applicable
     */
    private void addTestInvocation(String invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.addTestInvocation(invocation, this, testMethodName);
        }
    }


    /**
     * Overridden to install the tracing test listener.
     *
     * @return the unitils instance, not null
     */
    @Override
    protected Unitils getUnitils() {
        if (tracingTestListener != null) {
            return new Unitils() {

                @Override
                public TestListener getTestListener() {
                    return tracingTestListener;
                }
            };
        }
        return super.getUnitils();
    }

}
