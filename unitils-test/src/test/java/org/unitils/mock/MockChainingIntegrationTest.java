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
 * Tests chaining of methods when defining behavior and assertions (UNI-153).
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockChainingIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;


    @Test
    public void chainedBehavior() {
        mockObject.returns("value").getTestClass().getValue();

        String result = mockObject.getMock().getTestClass().getValue();
        assertEquals("value", result);
    }

    @Test
    public void chainedOnceReturns() {
        mockObject.returns("333").getTestClass().getValue();
        mockObject.onceReturns("111").getTestClass().getValue();
        mockObject.onceReturns("222").getTestClass().getValue();

        String result1 = mockObject.getMock().getTestClass().getValue();
        String result2 = mockObject.getMock().getTestClass().getValue();
        String result3 = mockObject.getMock().getTestClass().getValue();
        String result4 = mockObject.getMock().getTestClass().getValue();
        assertEquals("111", result1);
        assertEquals("222", result2);
        assertEquals("333", result3);
        assertEquals("333", result4);
    }

    @Test
    public void doubleChainedBehavior() {
        mockObject.returns("value").getTestClass().getTestClass().getValue();

        String result = mockObject.getMock().getTestClass().getTestClass().getValue();
        assertEquals("value", result);
    }

    @Test
    public void doubleChainedOnceReturns() {
        mockObject.onceReturns("111").getTestClass().getTestClass().getValue();
        mockObject.onceReturns("222").getTestClass().getValue();
        mockObject.returns("555").getTestClass().getTestClass().getValue();
        mockObject.returns("666").getTestClass().getValue();
        mockObject.onceReturns("333").getTestClass().getTestClass().getValue();
        mockObject.onceReturns("444").getTestClass().getValue();

        String result1 = mockObject.getMock().getTestClass().getTestClass().getValue();
        String result2 = mockObject.getMock().getTestClass().getValue();
        String result3 = mockObject.getMock().getTestClass().getTestClass().getValue();
        String result4 = mockObject.getMock().getTestClass().getValue();
        String result5 = mockObject.getMock().getTestClass().getTestClass().getValue();
        String result6 = mockObject.getMock().getTestClass().getValue();
        String result7 = mockObject.getMock().getTestClass().getTestClass().getValue();
        String result8 = mockObject.getMock().getTestClass().getValue();
        assertEquals("111", result1);
        assertEquals("222", result2);
        assertEquals("333", result3);
        assertEquals("444", result4);
        assertEquals("555", result5);
        assertEquals("666", result6);
        assertEquals("555", result7);
        assertEquals("666", result8);
    }

    @Test
    public void chainedAssertInvoked() {
        mockObject.returns("value").getTestClass().getValue();

        mockObject.getMock().getTestClass().getValue();
        mockObject.assertInvoked().getTestClass().getValue();
    }

    @Test
    public void multipleAssertInvoked() {
        mockObject.getMock().getTestClass();
        mockObject.getMock().getTestClass();
        mockObject.assertInvoked().getTestClass();
        mockObject.assertInvoked().getTestClass();
    }

    @Test
    public void chainedAssertNotInvoked() {
        mockObject.assertNotInvoked().getTestClass().getValue();
    }

    @Test
    public void chainedAssertNotInvokedButInvoked() {
        mockObject.returns("value").getTestClass().getValue();
        mockObject.getMock().getTestClass().getValue();
        try {
            mockObject.assertNotInvoked().getTestClass().getValue();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected no invocation of TestInterface.getTestClass(), but it did occur\n" +
                    "at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:118)\n" +
                    "Asserted at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:120)\n" +
                    "\n" +
                    "Observed scenario:\n" +
                    "\n" +
                    "1. mockObject.getTestClass() -> Proxy<mockObject.getTestClass>  .....  at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:118)\n" +
                    "2. mockObject.getTestClass.getValue() -> \"value\"  .....  at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:118)\n" +
                    "\n" +
                    "Detailed scenario:\n" +
                    "\n" +
                    "1. mockObject.getTestClass() -> Proxy<mockObject.getTestClass>\n" +
                    "- Observed at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:118)\n" +
                    "- Behavior defined at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:117)\n" +
                    "\n" +
                    "2. mockObject.getTestClass.getValue() -> \"value\"\n" +
                    "- Observed at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:118)\n" +
                    "- Behavior defined at org.unitils.mock.MockChainingIntegrationTest.chainedAssertNotInvokedButInvoked(MockChainingIntegrationTest.java:117)\n" +
                    "\n", e.getMessage());
        }
    }

    @Test
    public void chainedAssertInvokedInSequence() {
        mockObject.returns("value").getTestClass().getValue();

        mockObject.getMock().getTestClass().getValue();
        mockObject.getMock().getTestClass().getValue();
        mockObject.assertInvokedInSequence().getTestClass().getValue();
        mockObject.assertInvokedInSequence().getTestClass().getValue();
    }

    @Test
    public void chainedAssertInvokedInSequenceButNotInvoked() {
        mockObject.returns("value").getTestClass().getValue();
        mockObject.getMock().getTestClass().getValue();
        mockObject.assertInvokedInSequence().getTestClass().getValue();
        try {
            mockObject.assertInvokedInSequence().getTestClass().getValue();
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Expected invocation of TestInterface.getTestClass(), but it didn't occur.\n" +
                    "Asserted at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:161)\n" +
                    "\n" +
                    "Observed scenario:\n" +
                    "\n" +
                    "1. mockObject.getTestClass() -> Proxy<mockObject.getTestClass>  .....  at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:158)\n" +
                    "2. mockObject.getTestClass.getValue() -> \"value\"  .....  at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:158)\n" +
                    "\n" +
                    "Detailed scenario:\n" +
                    "\n" +
                    "1. mockObject.getTestClass() -> Proxy<mockObject.getTestClass>\n" +
                    "- Observed at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:158)\n" +
                    "- Behavior defined at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:157)\n" +
                    "\n" +
                    "2. mockObject.getTestClass.getValue() -> \"value\"\n" +
                    "- Observed at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:158)\n" +
                    "- Behavior defined at org.unitils.mock.MockChainingIntegrationTest.chainedAssertInvokedInSequenceButNotInvoked(MockChainingIntegrationTest.java:157)\n" +
                    "\n", e.getMessage());
        }
    }

    @Test
    public void multipleOperationsOnSameChainedMock() {
        mockObject.returns("value1").getTestClass().getValue();
        mockObject.returns("value2").getTestClass().getOtherValue();

        String result1 = mockObject.getMock().getTestClass().getValue();
        String result2 = mockObject.getMock().getTestClass().getOtherValue();

        assertEquals("value1", result1);
        assertEquals("value2", result2);
        mockObject.assertInvoked().getTestClass().getValue();
        mockObject.assertInvoked().getTestClass().getOtherValue();
    }


    private static interface TestInterface {

        String getValue();

        String getOtherValue();

        TestInterface getTestClass();
    }
}