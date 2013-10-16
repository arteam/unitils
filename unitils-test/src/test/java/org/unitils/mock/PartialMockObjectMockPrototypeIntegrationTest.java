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
import org.unitils.mock.core.ObservedInvocation;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PartialMockObjectMockPrototypeIntegrationTest {

    private PartialMock<TestClass> mockObject;


    @Before
    public void initialize() {
        TestClass prototype = new TestClass("original value");
        mockObject = MockUnitils.createPartialMock(prototype, this);
    }


    @Test
    public void mockedBehavior() {
        mockObject.returns("mocked value").testMethod();

        String result = mockObject.getMock().testMethod();
        assertEquals("mocked value", result);
    }

    @Test
    public void originalBehavior() {
        String result = mockObject.getMock().testMethod();
        assertEquals("original value", result);
    }

    @Test
    public void originalBehavior_methodWithArguments() {
        int result = mockObject.getMock().methodWithArguments(3, 4);
        assertEquals(17, result);
    }

    @Test
    public void overridingMethodCalledFromOtherMethod() {
        mockObject.returns(999).methodWithArguments(3, 4);

        int result = mockObject.getMock().methodThatCallsOtherMethod(3, 4);
        assertEquals(999, result);
        List<ObservedInvocation> observedInvocations = null;
        mockObject.assertInvokedInSequence().methodThatCallsOtherMethod(3, 4);
        mockObject.assertInvokedInSequence().methodWithArguments(3, 4);
    }


    private static class TestClass {

        private String someValue;
        private int c = 10;

        public TestClass(String someValue) {
            this.someValue = someValue;
        }

        public String testMethod() {
            return someValue;
        }

        protected int methodWithArguments(int a, int b) {
            return a + b + c;
        }

        public int methodThatCallsOtherMethod(int a, int b) {
            return methodWithArguments(a, b);
        }
    }
}