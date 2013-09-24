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
public class MockAssertInvokedInSequenceIntegrationTest extends UnitilsJUnit4 {

    private Mock<MyInterface> mockObject;


    @Test
    public void okWhenInvokedInCorrectSequence() {
        mockObject.getMock().testMethod1();
        mockObject.getMock().testMethod2();

        mockObject.assertInvokedInSequence().testMethod1();
        mockObject.assertInvokedInSequence().testMethod2();
    }

    @Test
    public void exceptionWhenInvokedOutOfSequence() {
        mockObject.getMock().testMethod1();
        mockObject.getMock().testMethod2();

        mockObject.assertInvokedInSequence().testMethod2();
        try {
            mockObject.assertInvokedInSequence().testMethod1();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Invocation of MyInterface.testMethod1() was expected to be performed after MyInterface.testMethod2() but actually occurred before it.\n" +
                    "asserted at org.unitils.mock.MockAssertInvokedInSequenceIntegrationTest.exceptionWhenInvokedOutOfSequence(MockAssertInvokedInSequenceIntegrationTest.java:49)\n" +
                    "\n" +
                    "Observed scenario:\n" +
                    "\n" +
                    "1. mockObject.testMethod1()  .....  at org.unitils.mock.MockAssertInvokedInSequenceIntegrationTest.exceptionWhenInvokedOutOfSequence(MockAssertInvokedInSequenceIntegrationTest.java:44)\n" +
                    "2. mockObject.testMethod2()  .....  at org.unitils.mock.MockAssertInvokedInSequenceIntegrationTest.exceptionWhenInvokedOutOfSequence(MockAssertInvokedInSequenceIntegrationTest.java:45)\n" +
                    "\n" +
                    "\n" +
                    "Detailed scenario:\n" +
                    "\n" +
                    "1. mockObject.testMethod1()\n" +
                    "- Observed at org.unitils.mock.MockAssertInvokedInSequenceIntegrationTest.exceptionWhenInvokedOutOfSequence(MockAssertInvokedInSequenceIntegrationTest.java:44)\n" +
                    "\n" +
                    "2. mockObject.testMethod2()\n" +
                    "- Observed at org.unitils.mock.MockAssertInvokedInSequenceIntegrationTest.exceptionWhenInvokedOutOfSequence(MockAssertInvokedInSequenceIntegrationTest.java:45)\n" +
                    "\n", e.getMessage());
            StackTraceElement topOfStackTrace = e.getStackTrace()[0];
            assertEquals(MockAssertInvokedInSequenceIntegrationTest.class.getName(), topOfStackTrace.getClassName());
            assertEquals("exceptionWhenInvokedOutOfSequence", topOfStackTrace.getMethodName());
        }
    }


    private static interface MyInterface {

        void testMethod1();

        void testMethod2();
    }
}