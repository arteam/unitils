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

import static org.unitils.TracingTestListener.*;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;


/**
 * JUnit 3 test class containing 2 test methods
 */
public class UnitilsJUnit3Test_TestClass1 extends UnitilsJUnit3 {

    private static TracingTestListener tracingTestListener;

    public static void setTracingTestListener(TracingTestListener testListener) {
        tracingTestListener = testListener;
    }


    protected void setUp() throws Exception {
        super.setUp();
        addTestInvocation(TEST_SET_UP, null);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        addTestInvocation(TEST_TEAR_DOWN, null);
    }

    public void test1() {
        addTestInvocation(TEST_METHOD, "test1");
    }

    public void test2() {
        addTestInvocation(TEST_METHOD, "test2");
    }


    private void addTestInvocation(String invocation, String testMethodName) {
        if (tracingTestListener != null) {
            tracingTestListener.addTestInvocation(invocation, this, testMethodName);
        }
    }


    @Override
    protected Unitils getUnitils() {
        if (tracingTestListener != null) {
            return new Unitils() {

                public TestListener createTestListener() {
                    return tracingTestListener;
                }
            };
        }
        return super.getUnitils();
    }
}
