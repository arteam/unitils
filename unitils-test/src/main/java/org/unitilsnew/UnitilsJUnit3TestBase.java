/*
 * Copyright 2013,  Unitils.org
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
package org.unitilsnew;

import org.unitils.UnitilsJUnit3;

import static org.unitilsnew.TracingTestListener.TestInvocation.TEST_SET_UP;
import static org.unitilsnew.TracingTestListener.TestInvocation.TEST_TEAR_DOWN;

/**
 * Base class for the JUnit3 test listener tests.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public abstract class UnitilsJUnit3TestBase extends UnitilsJUnit3 {

    /* Test listener that will record all invocations */
    protected static TracingTestListener tracingTestListener;


    /**
     * Sets the tracing test listener that will record all invocations.
     *
     * @param testListener the listener
     */
    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    /**
     * Overidden to register the test setup invocation.
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        registerTestInvocation(TEST_SET_UP, null);
    }

    /**
     * Overidden to register the test teardown invocation.
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        registerTestInvocation(TEST_TEAR_DOWN, null);
    }

    /**
     * Records an invocation.
     *
     * @param invocation the invocation type, not null
     * @param testMethod The called method, not null
     */
    protected void registerTestInvocation(TracingTestListener.TestInvocation invocation, String testMethod) {
        if (tracingTestListener != null) {
            tracingTestListener.registerTestInvocation(invocation, getClass(), testMethod);
        }
    }
}
