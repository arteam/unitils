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

import org.testng.annotations.*;
import static org.unitils.TracingTestListener.TestInvocation.TEST_BEFORE_CLASS; 
import static org.unitils.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitils.TracingTestListener.TestInvocation.TEST_METHOD;
import static org.unitils.TracingTestListener.TestInvocation.TEST_TEAR_DOWN; 
import static org.unitils.TracingTestListener.TestInvocation.TEST_AFTER_CLASS;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * TestNG test class containing an active and 1 ignored test method belonging to the group 'testGroup' and 1 test method
 * belonging to another group. This test test-class is used in the {@link UnitilsInvocationTest} and
 * {@link UnitilsInvocationExceptionTest} tests.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsTestNGTest_GroupsTest extends UnitilsTestNG {


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


    @BeforeClass(groups = "testGroup")
    public void beforeClass() {
        addTestInvocation(TEST_BEFORE_CLASS, null);
    }


    @AfterClass(groups = "testGroup")
    public void afterClass() {
        addTestInvocation(TEST_AFTER_CLASS, null);
    }


    @BeforeMethod(groups = "testGroup")
    public void setUp() {
        addTestInvocation(TEST_SET_UP, null);
    }


    @AfterMethod(groups = "testGroup")
    public void tearDown() {
        addTestInvocation(TEST_TEAR_DOWN, null);
    }


    @Test(groups = "testGroup")
    public void test1() {
        addTestInvocation(TEST_METHOD, "test1");
    }


    @Test(enabled = false, groups = "testGroup")
    public void test2() {
        addTestInvocation(TEST_METHOD, "test2");
    }

    @Test(enabled = false, groups = "otherGroup")
    public void test3() {
        addTestInvocation(TEST_METHOD, "test3");
    }

    /**
     * Records an invocation.
     *
     * @param invocation     the invocation type, not null
     * @param testMethodName the actual test name, null if not applicable
     */
    private void addTestInvocation(TracingTestListener.TestInvocation invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.registerTestInvocation(invocation, this.getClass(), testMethodName);
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
