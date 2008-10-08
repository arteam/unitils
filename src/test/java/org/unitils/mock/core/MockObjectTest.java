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

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tests the mock object functionality.
 *
 * todo tests for when no test method is called after fe returns()
 * e.g. mockObject.returns("aValue");
 */
public class MockObjectTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, false, new Scenario());
    }


    /**
     * Tests setting a return behavior for the mock. The behavior is an always matching behavior
     * so the method should keep returning that same value.
     */
    @Test
    public void testReturns() {
        mockObject.returns("aValue").testMethodString();

        String result1 = mockObject.getInstance().testMethodString();
        String result2 = mockObject.getInstance().testMethodString();
        assertLenEquals("aValue", result1);
        assertLenEquals("aValue", result2);
    }


    /**
     * Tests setting a once return behavior for the mock. The behavior should be executed only once, the second time
     * the default null value is returned.
     */
    @Test
    public void testOnceReturns() {
        mockObject.onceReturns("aValue").testMethodString();

        String result1 = mockObject.getInstance().testMethodString();
        String result2 = mockObject.getInstance().testMethodString();
        assertLenEquals("aValue", result1);
        assertNull(result2);
    }


    /**
     * Tests the return behavior when no behavior was defined. The null value should be
     * returned as default object value.
     */
    @Test
    public void testReturns_defaultBehaviorObject() {
        String result = mockObject.getInstance().testMethodString();
        assertLenEquals(null, result);
    }


    /**
     * Tests the return behavior when no behavior was defined. The 0 value should be
     * returned as default number value.
     */
    @Test
    public void testReturns_defaultBehaviorNumber() {
        int result = mockObject.getInstance().testMethodNumber();
        assertLenEquals(0, result);
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty list should be
     * returned as default list value.
     */
    @Test
    public void testReturns_defaultBehaviorList() {
        List<String> result = mockObject.getInstance().testMethodList();
        assertLenEquals(0, result.size());
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty set should be
     * returned as default set value.
     */
    @Test
    public void testReturns_defaultBehaviorSet() {
        Set<String> result = mockObject.getInstance().testMethodSet();
        assertLenEquals(0, result.size());
    }


    /**
     * Tests the return behavior when no behavior was defined. An empty map should be
     * returned as default map value.
     */
    @Test
    public void testReturns_defaultBehaviorMap() {
        Map<String, String> result = mockObject.getInstance().testMethodMap();
        assertLenEquals(0, result.size());
    }


    /**
     * Tests setting an exception behavior for the mock. The behavior is an always matching behavior
     * so the method should keep throwing that same exception.
     */
    @Test
    public void testRaises() {
        mockObject.raises(new ThreadDeath()).testMethodString();

        boolean exception1 = false;
        try {
            mockObject.getInstance().testMethodString();
        } catch (ThreadDeath e) {
            exception1 = true;
        }
        boolean exception2 = false;
        try {
            mockObject.getInstance().testMethodString();
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
        mockObject.onceRaises(new ThreadDeath()).testMethodString();

        boolean exception1 = false;
        try {
            mockObject.getInstance().testMethodString();
        } catch (ThreadDeath e) {
            exception1 = true;
        }
        boolean exception2 = false;
        try {
            mockObject.getInstance().testMethodString();
        } catch (ThreadDeath e) {
            exception2 = true;
        }
        assertTrue(exception1);
        assertFalse(exception2);
    }
    
    
    @Test
    public void testRaises_exceptionClass() {
        mockObject.raises(IllegalArgumentException.class).testMethodString();
        
        try {
            mockObject.getInstance().testMethodString();
            fail("Expected exception");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
    
    
    @Test
    public void testOnceRaises_exceptionClass() {
        mockObject.onceRaises(IllegalArgumentException.class).testMethodString();
        
        try {
            mockObject.getInstance().testMethodString();
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
        mockObject.performs(testMockBehavior).testMethodString();

        mockObject.getInstance().testMethodString();
        mockObject.getInstance().testMethodString();
        assertLenEquals(2, testMockBehavior.invocationCount);
    }


    /**
     * Tests setting a once peforms behavior for the mock. The behavior should be executed only once, the second time
     * nothing should have happened.
     */
    @Test
    public void testOncePerforms() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject.oncePerforms(testMockBehavior).testMethodString();

        mockObject.getInstance().testMethodString();
        mockObject.getInstance().testMethodString();
        assertLenEquals(1, testMockBehavior.invocationCount);
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
}
