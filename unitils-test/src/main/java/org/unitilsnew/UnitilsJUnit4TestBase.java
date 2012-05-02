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
package org.unitilsnew;


/**
 * Base class for the JUnit4 test listener tests.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public abstract class UnitilsJUnit4TestBase extends UnitilsJUnit4 {

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
     * @param invocation     The invocation type, not null
     * @param testClass      The test class, not null
     * @param testMethodName The called method, null if not applicable
     */
    protected static void registerTestInvocation(TracingTestListener.TestInvocation invocation, Class<?> testClass, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.registerTestInvocation(invocation, testClass, testMethodName);
        }
    }

}
