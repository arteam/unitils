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
import static org.unitils.mock.MockUnitils.assertNoMoreInvocations;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockUnitilsAssertNoMoreInvocationsIntegrationTest extends UnitilsJUnit4 {

    /* Class under test */
    private Mock<TestInterface> mockObject;


    @Test
    public void okWhenNoMoreInvocations() {
        mockObject.getMock().testMethod();

        mockObject.assertInvoked().testMethod();
        assertNoMoreInvocations();
    }

    @Test
    public void okWhenNoInvocations() {
        assertNoMoreInvocations();
    }

    @Test
    public void exceptionWhenStillMoreInvocations() {
        mockObject.getMock().testMethod();
        mockObject.getMock().testMethod();

        mockObject.assertInvoked().testMethod();
        try {
            assertNoMoreInvocations();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("No more invocations expected, yet observed following calls:\n" +
                    "1. mockObject.testMethod()  .....  at org.unitils.mock.MockUnitilsAssertNoMoreInvocationsIntegrationTest.exceptionWhenStillMoreInvocations(MockUnitilsAssertNoMoreInvocationsIntegrationTest.java:51)\n" +
                    "\n" +
                    "Observed scenario:\n" +
                    "\n" +
                    "1. mockObject.testMethod()  .....  at org.unitils.mock.MockUnitilsAssertNoMoreInvocationsIntegrationTest.exceptionWhenStillMoreInvocations(MockUnitilsAssertNoMoreInvocationsIntegrationTest.java:50)\n" +
                    "2. mockObject.testMethod()  .....  at org.unitils.mock.MockUnitilsAssertNoMoreInvocationsIntegrationTest.exceptionWhenStillMoreInvocations(MockUnitilsAssertNoMoreInvocationsIntegrationTest.java:51)\n" +
                    "\n" +
                    "Detailed scenario:\n" +
                    "\n" +
                    "1. mockObject.testMethod()\n" +
                    "- Observed at org.unitils.mock.MockUnitilsAssertNoMoreInvocationsIntegrationTest.exceptionWhenStillMoreInvocations(MockUnitilsAssertNoMoreInvocationsIntegrationTest.java:50)\n" +
                    "\n" +
                    "2. mockObject.testMethod()\n" +
                    "- Observed at org.unitils.mock.MockUnitilsAssertNoMoreInvocationsIntegrationTest.exceptionWhenStillMoreInvocations(MockUnitilsAssertNoMoreInvocationsIntegrationTest.java:51)\n" +
                    "\n", e.getMessage());
        }
    }


    private static interface TestInterface {

        void testMethod();
    }
}