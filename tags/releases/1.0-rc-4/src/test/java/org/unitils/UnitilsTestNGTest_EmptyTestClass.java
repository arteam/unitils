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

import org.unitils.core.TestListener;
import org.unitils.core.Unitils;

/**
 * Empty TestNG test class. This test test-class is used in the {@link UnitilsInvocationTest} tests.
 *
 * @author Tim Ducheyne
 */
public class UnitilsTestNGTest_EmptyTestClass extends UnitilsTestNG {


    /* Test listener that will record all invocations */
    private static TracingTestListener tracingTestListener;


    /**
     * Sets the tracing test listener that will record all invocations .
     *
     * @param testListener the listener
     */
    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
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
                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
        return super.getUnitils();
    }

}
