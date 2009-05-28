/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core;

import static junit.framework.Assert.*;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.util.CloneUtil;
import static org.unitils.mock.ArgumentMatchers.notNull;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

/**
 * Tests the mock object functionality.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockObjectTest {

    /* Class under test */
    private MockObject<TestClass> mockObject1, mockObject2;
    private MockObject<MockReturning> mockReturningOtherMock;


    @Before
    public void setUp() {
        Scenario scenario = new Scenario(null);
        mockObject1 = new MockObject<TestClass>("testMock", TestClass.class, false, scenario);
        mockObject2 = new MockObject<TestClass>("testMock", TestClass.class, false, scenario);
        mockReturningOtherMock = new MockObject<MockReturning>("testMock", MockReturning.class, false, scenario);
    }


    @Test
    public void testInvocationSpreadOverMoreThanOneLine() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();

        mockObject1.performs( //
                testMockBehavior
        ).      //
                testMethod(//
                notNull(//
                        String.class//
                ));

        mockObject1. //
                getMock().//
                testMethod(//
                null);
        assertEquals(0, testMockBehavior.invocationCount);

        mockObject1.getMock().//
                testMethod( //
                "value");
        assertEquals(1, testMockBehavior.invocationCount);
    }


    @Test
    public void testAssertInvokedFailure() {
        try {
            mockObject1.assertInvoked().testMethod(null);
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testAssertInvokedFailure");
        }
    }


    @Test
    public void testAssertNotInvokedFailure() {
        try {
            mockObject1.getMock().testMethod(null);
            mockObject1.assertNotInvoked().testMethod(null);
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testAssertNotInvokedFailure");
        }
    }


    @Test
    public void testAssertInvokedInSequenceFailure() {
        try {
            mockObject1.assertInvokedInSequence().testMethodArray();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testAssertInvokedInSequenceFailure");
        }
    }


    @Test
    public void testEquals() {
        assertTrue(mockObject1.getMock().equals(mockObject1.getMock()));
        assertFalse(mockObject1.getMock().equals(mockObject2.getMock()));
    }


    @Test
    public void testHashcode_equalObjects() {
        assertTrue(mockObject1.getMock().hashCode() == mockObject1.getMock().hashCode());
    }


    @Test
    public void testHashcode_differentObjects() {
        assertFalse(mockObject1.getMock().hashCode() == mockObject2.getMock().hashCode());
    }


    @Test
    public void testDeepCloneEqualToOriginal() {
        assertTrue(mockObject1.getMock() instanceof Cloneable);
        TestClass clone = CloneUtil.createDeepClone(mockObject1.getMock());
        assertEquals(mockObject1.getMock(), clone);
    }


    /**
     * Test written for bugfix: if a mocked method returns another mock object, this other mock object is cloned
     * at each invocation and added to the scenario. Because the mock object references the scenario, the scenario
     * itself is also cloned each time. The result is that, at each invocation, the amount of objects which is cloned
     * is twice the amount of the previous one. This quickly results in a large time delay. The bug fix involves that
     * the mock object proxy now implements the {@link Cloneable} interface and the {@link Object#clone()} method
     * returns the mock object proxy itself.
     */
    @Test(timeout = 1000)
    public void testMockThatReturnsOtherMock() {
        mockReturningOtherMock.returns(mockObject1.getMock()).getOtherMock();

        for (int i = 0; i < 50; i++) {
            mockReturningOtherMock.getMock().getOtherMock().testMethod(null);
        }
    }


    private void assertTopOfStackTracePointsToCurrentTest(Throwable e, Object testMethodName) {
        StackTraceElement topOfStackTrace = e.getStackTrace()[0];
        assertEquals(MockObjectTest.class.getName(), topOfStackTrace.getClassName());
        assertEquals(testMethodName, topOfStackTrace.getMethodName());
    }


    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethod(String arg);

        public int[] testMethodArray();

        public Object clone() throws CloneNotSupportedException;
    }


    /**
     * Dummy mock behavior that counts how many times it was invoked.
     */
    private static class TestMockBehavior implements MockBehavior {

        public int invocationCount = 0;

        public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
            invocationCount++;
            return null;
        }
    }


    private static interface MockReturning {

        TestClass getOtherMock();
    }
}
