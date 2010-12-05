/*
 * Copyright Unitils.org
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

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * Tests the mock object functionality for partial mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PartialMockObjectTest {

    /* Class under test */
    private PartialMockObject<TestClass> mockObject;


    /**
     * Initializes the test by creating a partial mock object
     */
    @Before
    public void setUp() {
        TestClass.invocationCount = 0;
        mockObject = new PartialMockObject<TestClass>("testMock", TestClass.class, this);
    }


    /**
     * Tests setting a return behavior for the mock. The behavior of the test class should not have been invoked.
     */
    @Test
    public void testReturns() {
        mockObject.returns("aValue").testMethod();

        String result = mockObject.getMock().testMethod();
        assertLenientEquals("aValue", result);
        assertLenientEquals(0, TestClass.invocationCount);
    }


    /**
     * Tests the return behavior when no behavior was defined. The original behavior of the test class should
     * have been invoked.
     */
    @Test
    public void testReturns_originalBehavior() {
        String result = mockObject.getMock().testMethod();
        assertLenientEquals("original", result);
        assertLenientEquals(1, TestClass.invocationCount);
    }


    /**
     * Tests setting an exception behavior for the mock. The behavior of the test class should not have been invoked.
     */
    @Test
    public void testRaises() {
        mockObject.raises(new ThreadDeath()).testMethod();

        boolean exception = false;
        try {
            mockObject.getMock().testMethod();
        } catch (ThreadDeath e) {
            exception = true;
        }
        assertTrue(exception);
        assertLenientEquals(0, TestClass.invocationCount);
    }


    /**
     * Tests setting a performs behavior for the mock. The behavior of the test class should not have been invoked.
     */
    @Test
    public void testPerforms() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject.performs(testMockBehavior).testMethod();

        mockObject.getMock().testMethod();
        assertLenientEquals(1, testMockBehavior.invocationCount);
        assertLenientEquals(0, TestClass.invocationCount);
    }


    /**
     * Tests invoking a method for with no behavior was defined. The behavior of the test class should have been invoked.
     */
    @Test
    public void originalBehavior() {
        String result = mockObject.getMock().testMethod();
        assertLenientEquals("original", result);
        assertLenientEquals(1, TestClass.invocationCount);
    }


    /**
     * Tests invoking a method for with no behavior was defined, but the method is an abstract method.
     * An exception should have been raised
     */
    @Test(expected = UnitilsException.class)
    public void originalBehavior_abstractMethod() {
        mockObject.getMock().abstractMethod();
    }

    @Test
    public void originalBehavior_methodWithArguments() {
        int result = mockObject.getMock().methodWithArguments(3, 4);
        assertEquals(7, result);
    }


    /**
     * Class that is mocked during the tests. The test method counts how many times it was invoked.
     */
    public static abstract class TestClass {

        public static int invocationCount = 0;

        public String testMethod() {
            invocationCount++;
            return "original";
        }

        public int methodWithArguments(int a, int b) {
            return a + b;
        }

        public abstract void abstractMethod();
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