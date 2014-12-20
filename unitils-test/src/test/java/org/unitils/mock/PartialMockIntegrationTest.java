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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.core.proxy.ProxyInvocation;
import org.unitils.mock.mockbehavior.MockBehavior;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;

/**
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
    public void ignoredWhenNoBehaviorSetForVoidAbstractMethod() {
        mockObject.getMock().voidAbstractMethod();
    }

    @Test
    public void defaultValueForAbstractMethodWithReturnValue() {
        List result = mockObject.getMock().returnAbstractMethod();
        assertTrue(result instanceof List);
    }

    @Test
    public void originalBehavior_methodWithArguments() {
        int result = mockObject.getMock().methodWithArguments(3, 4);
        assertEquals(7, result);
    }

    @Test
    public void overridingMethodCalledFromOtherMethod() {
        mockObject.returns(999).methodWithArguments(3, 4);

        int result = mockObject.getMock().methodThatCallsOtherMethod(3, 4);
        assertEquals(999, result);
    }


    public static abstract class TestClass {

        public static int invocationCount = 0;

        public String testMethod() {
            invocationCount++;
            return "original";
        }

        public int methodWithArguments(int a, int b) {
            return a + b;
        }

        public int methodThatCallsOtherMethod(int a, int b) {
            return methodWithArguments(a, b);
        }

        public abstract void voidAbstractMethod();

        public abstract List returnAbstractMethod();
    }

    private static class TestMockBehavior implements MockBehavior {

        public int invocationCount = 0;

        public Object execute(ProxyInvocation proxyInvocation) throws Throwable {
            invocationCount++;
            return null;
        }
    }
}