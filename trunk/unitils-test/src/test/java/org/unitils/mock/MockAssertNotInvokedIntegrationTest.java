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
public class MockAssertNotInvokedIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;


    @Test
    public void okWhenMethodWasNotInvoked() {
        mockObject.assertNotInvoked().testMethod();
    }

    @Test
    public void exceptionWhenMethodWasInvoked() {
        mockObject.getMock().testMethod();
        try {
            mockObject.assertNotInvoked().testMethod();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected no invocation of TestInterface.testMethod(), but it did occur.\n" +
                    "at org.unitils.mock.MockAssertNotInvokedIntegrationTest.exceptionWhenMethodWasInvoked(MockAssertNotInvokedIntegrationTest.java:40)\n" +
                    "asserted at org.unitils.mock.MockAssertNotInvokedIntegrationTest.exceptionWhenMethodWasInvoked(MockAssertNotInvokedIntegrationTest.java:42)\n" +
                    "\n" +
                    "Observed scenario:\n" +
                    "\n" +
                    "1. mockObject.testMethod()  .....  at org.unitils.mock.MockAssertNotInvokedIntegrationTest.exceptionWhenMethodWasInvoked(MockAssertNotInvokedIntegrationTest.java:40)\n" +
                    "\n" +
                    "\n" +
                    "Detailed scenario:\n" +
                    "\n" +
                    "1. mockObject.testMethod()\n" +
                    "- Observed at org.unitils.mock.MockAssertNotInvokedIntegrationTest.exceptionWhenMethodWasInvoked(MockAssertNotInvokedIntegrationTest.java:40)\n" +
                    "\n", e.getMessage());
            StackTraceElement topOfStackTrace = e.getStackTrace()[0];
            assertEquals(MockAssertNotInvokedIntegrationTest.class.getName(), topOfStackTrace.getClassName());
            assertEquals("exceptionWhenMethodWasInvoked", topOfStackTrace.getMethodName());
        }
    }


    private static interface TestInterface {

        void testMethod();
    }
}