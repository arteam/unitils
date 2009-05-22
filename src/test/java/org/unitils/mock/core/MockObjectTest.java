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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.util.CloneUtil;
import static org.unitils.mock.ArgumentMatchers.notNull;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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


    /**
     * Tests setting a return behavior for the mock. The behavior is an always matching behavior
     * so the method should keep returning that same value.
     */
    @Test
    public void testReturns() {
        mockObject1.returns("aValue").testMethodString();

        String result1 = mockObject1.getMock().testMethodString();
        String result2 = mockObject1.getMock().testMethodString();
        assertLenientEquals("aValue", result1);
        assertLenientEquals("aValue", result2);
    }


    /**
     * Tests setting a once return behavior for the mock. The behavior should be executed only once, the second time
     * the default null value is returned.
     */
    @Test
    public void testOnceReturns() {
        mockObject1.onceReturns("aValue").testMethodString();

        String result1 = mockObject1.getMock().testMethodString();
        String result2 = mockObject1.getMock().testMethodString();
        assertLenientEquals("aValue", result1);
        assertNull(result2);
    }


    /**
     * Tests the return behavior when no behavior was defined. The null value should be
     * returned as default object value.
     */
    @Test
    public void testReturns_defaultBehaviorObject() {
        String result = mockObject1.getMock().testMethodString();
        assertLenientEquals(null, result);
    }


    /**
     * Tests the return behavior when no behavior was defined. The 0 value should be
     * returned as default number value.
     */
    @Test
    public void testReturns_defaultBehaviorNumber() {
        int result = mockObject1.getMock().testMethodNumber();
        assertLenientEquals(0, result);
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty list should be
     * returned as default list value.
     */
    @Test
    public void testReturns_defaultBehaviorList() {
        List<String> result = mockObject1.getMock().testMethodList();
        assertLenientEquals(0, result.size());
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty set should be
     * returned as default set value.
     */
    @Test
    public void testReturns_defaultBehaviorSet() {
        Set<String> result = mockObject1.getMock().testMethodSet();
        assertLenientEquals(0, result.size());
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty map should be
     * returned as default map value.
     */
    @Test
    public void testReturns_defaultBehaviorMap() {
        Map<String, String> result = mockObject1.getMock().testMethodMap();
        assertLenientEquals(0, result.size());
    }


    /**
     * Tests setting an exception behavior for the mock. The behavior is an always matching behavior
     * so the method should keep throwing that same exception.
     */
    @Test
    public void testRaises() {
        mockObject1.raises(new ThreadDeath()).testMethodString();

        boolean exception1 = false;
        try {
            mockObject1.getMock().testMethodString();
        } catch (ThreadDeath e) {
            exception1 = true;
        }
        boolean exception2 = false;
        try {
            mockObject1.getMock().testMethodString();
        } catch (ThreadDeath e) {
            exception2 = true;
        }
        assertTrue(exception1);
        assertTrue(exception2);
    }


    /**
     * Tests setting an once exception behavior for the mock. The behavior should be executed only once, the second time
     * no exception should be raised.
     */
    @Test
    public void testOnceRaises() {
        mockObject1.onceRaises(new ThreadDeath()).testMethodString();

        boolean exception1 = false;
        try {
            mockObject1.getMock().testMethodString();
        } catch (ThreadDeath e) {
            exception1 = true;
        }
        boolean exception2 = false;
        try {
            mockObject1.getMock().testMethodString();
        } catch (ThreadDeath e) {
            exception2 = true;
        }
        assertTrue(exception1);
        assertFalse(exception2);
    }


    @Test
    public void testRaises_exceptionClass() {
        mockObject1.raises(IllegalArgumentException.class).testMethodString();

        try {
            mockObject1.getMock().testMethodString();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }


    @Test
    public void testOnceRaises_exceptionClass() {
        mockObject1.onceRaises(IllegalArgumentException.class).testMethodString();

        try {
            mockObject1.getMock().testMethodString();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }


    /**
     * Tests setting a peforms behavior for the mock. The behavior is an always matching behavior
     * so the method should keep performing that same behavior.
     */
    @Test
    public void testPerforms() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject1.performs(testMockBehavior).testMethodString();

        mockObject1.getMock().testMethodString();
        mockObject1.getMock().testMethodString();
        assertLenientEquals(2, testMockBehavior.invocationCount);
    }


    /**
     * Tests setting a once peforms behavior for the mock. The behavior should be executed only once, the second time
     * nothing should have happened.
     */
    @Test
    public void testOncePerforms() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject1.oncePerforms(testMockBehavior).testMethodString();

        mockObject1.getMock().testMethodString();
        mockObject1.getMock().testMethodString();
        assertLenientEquals(1, testMockBehavior.invocationCount);
    }


    @SuppressWarnings("unchecked")
    @Test
    public void testInvocationSpreadOverMoreThanOneLine() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject1.performs(testMockBehavior).testMethodParam(
                notNull(List.class));
        mockObject1.getMock().testMethodParam(null);
        assertEquals(0, testMockBehavior.invocationCount);
        mockObject1.getMock().testMethodParam(new ArrayList<String>());
        assertEquals(1, testMockBehavior.invocationCount);
    }

    @Test
    public void testCorrectAssertionFailedStackTrace() {
        try {
            mockObject1.assertInvoked().testMethodString();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testCorrectAssertionFailedStackTrace");
        }

        try {
            mockObject1.getMock().testMethodString();
            mockObject1.assertNotInvoked().testMethodString();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testCorrectAssertionFailedStackTrace");
        }

        try {
            mockObject1.assertInvokedInSequence().testMethodArray();
            fail();
        } catch (AssertionError e) {
            assertTopOfStackTracePointsToCurrentTest(e, "testCorrectAssertionFailedStackTrace");
        }
    }

    @Test
    public void testEquals() {
        assertTrue(mockObject1.getMock().equals(mockObject1.getMock()));
        assertFalse(mockObject1.getMock().equals(mockObject2.getMock()));
    }

    @Test
    public void testHashcode() {
        assertTrue(mockObject1.getMock().hashCode() == mockObject1.getMock().hashCode());
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
            mockReturningOtherMock.getMock().getOtherMock().testMethodString();
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

        public String testMethodString();

        public int testMethodNumber();

        public List<String> testMethodList();

        public Set<String> testMethodSet();

        public Map<String, String> testMethodMap();

        public int[] testMethodArray();

        public void testMethodParam(List<String> param);

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
