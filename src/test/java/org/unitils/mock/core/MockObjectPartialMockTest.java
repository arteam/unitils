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

import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;

/**
 * Tests the mock object functionality for partial mocks.
 *
 * todo test for abstract/interface methods
 */
public class MockObjectPartialMockTest {

    /* Class under test */
    private MockObject<TestClass> mockObject;


    /**
     * Initializes the test by creating a partial mock object
     */
    @Before
    public void setUp() {
        TestClass.invocationCount = 0;
        mockObject = new MockObject<TestClass>("testMock", TestClass.class, true, new Scenario());
    }


    /**
     * Tests setting a return behavior for the mock. The behavior of the test class should not have been invoked.
     */
    @Test
    public void testReturns() {
        mockObject.returns("aValue").testMethod();

        String result = mockObject.getInstance().testMethod();
        assertLenEquals("aValue", result);
        assertLenEquals(0, TestClass.invocationCount);
    }


    /**
     * Tests the return behavior when no behavior was defined. The original behavior of the test class should
     * have been invoked.
     */
    @Test
    public void testReturns_originalBehavior() {
        String result = mockObject.getInstance().testMethod();
        assertLenEquals("original", result);
        assertLenEquals(1, TestClass.invocationCount);
    }


    /**
     * Tests setting an exception behavior for the mock. The behavior of the test class should not have been invoked.
     */
    @Test
    public void testRaises() {
        mockObject.raises(new ThreadDeath()).testMethod();

        boolean exception = false;
        try {
            mockObject.getInstance().testMethod();
        } catch (ThreadDeath e) {
            exception = true;
        }
        assertTrue(exception);
        assertLenEquals(0, TestClass.invocationCount);
    }


    /**
     * Tests setting a peforms behavior for the mock. The behavior of the test class should not have been invoked.
     */
    @Test
    public void testPerforms() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject.performs(testMockBehavior).testMethod();

        mockObject.getInstance().testMethod();
        assertLenEquals(1, testMockBehavior.invocationCount);
        assertLenEquals(0, TestClass.invocationCount);
    }


    /**
     * Class that is mocked during the tests. The test method counts how many times it was invoked.
     */
    public static class TestClass {

        public static int invocationCount = 0;

        public String testMethod() {
            invocationCount++;
            return "original";
        }
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