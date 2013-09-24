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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockAssertInvokedIntegrationTest extends UnitilsJUnit4 {

    /* Class under test */
    private Mock<TestInterface> mockObject;


    @Test
    public void okWhenMethodWasInvoked() {
        mockObject.getMock().testMethod();
        mockObject.assertInvoked().testMethod();
    }

    @Test
    public void exceptionWhenMethodWasNotInvoked() {
        try {
            mockObject.assertInvoked().testMethod();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected invocation of TestInterface.testMethod(), but it didn't occur.\n" +
                    "asserted at org.unitils.mock.MockAssertInvokedIntegrationTest.exceptionWhenMethodWasNotInvoked(MockAssertInvokedIntegrationTest.java:43)\n" +
                    "\n" +
                    "No invocations observed.\n", e.getMessage());
            StackTraceElement topOfStackTrace = e.getStackTrace()[0];
            assertEquals(MockAssertInvokedIntegrationTest.class.getName(), topOfStackTrace.getClassName());
            assertEquals("exceptionWhenMethodWasNotInvoked", topOfStackTrace.getMethodName());
        }
    }


    private static interface TestInterface {

        void testMethod();
    }
}