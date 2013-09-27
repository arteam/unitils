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
package org.unitils.mock.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.PartialMock;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
 * Tests the mock object functionality for partial mocks.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PartialMockIntegrationTest extends UnitilsJUnit4 {

    private PartialMock<TestClass> mockObject;


    @Before
    public void initialize() {
        TestClass.invocationCount = 0;
    }


    @Test
    public void returns() {
        mockObject.returns("aValue").testMethod();

        String result = mockObject.getMock().testMethod();
        assertLenientEquals("aValue", result);
        assertLenientEquals(0, TestClass.invocationCount);
    }

    @Test
    public void originalBehavior() {
        String result = mockObject.getMock().testMethod();
        assertLenientEquals("original", result);
        assertLenientEquals(1, TestClass.invocationCount);
    }

    @Test
    public void raises() {
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

    @Test
    public void performs() {
        TestMockBehavior testMockBehavior = new TestMockBehavior();
        mockObject.performs(testMockBehavior).testMethod();

        mockObject.getMock().testMethod();
        assertLenientEquals(1, testMockBehavior.invocationCount);
        assertLenientEquals(0, TestClass.invocationCount);
    }

    @Test
    public void exceptionWhenNoBehaviorSetForAbstractMethod() {
        try {
            mockObject.getMock().abstractMethod();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Cannot invoke original behavior of an abstract method. Method: public abstract void org.unitils.mock.core.PartialMockIntegrationTest$TestClass.abstractMethod()", e.getMessage());
        }
    }


    public static abstract class TestClass {

        public static int invocationCount = 0;

        public String testMethod() {
            invocationCount++;
            return "original";
        }

        public abstract void abstractMethod();
    }

    public static class TestMockBehavior implements MockBehavior {

        public int invocationCount = 0;

        public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
            invocationCount++;
            return null;
        }
    }
}