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

import static org.junit.Assert.assertEquals;

/**
 * Tests the mock object functionality for partial mocks that wrap around an existing instance.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PartialMockObjectMockedInstanceTest {

    /* Class under test */
    private PartialMockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        TestClass mockedInstance = new TestClass("original value");
        mockObject = new PartialMockObject<TestClass>(mockedInstance, this);
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


    private static class TestClass {

        private String someValue;
        private int c = 10;

        public TestClass(String someValue) {
            this.someValue = someValue;
        }

        public String testMethod() {
            return someValue;
        }

        public int methodWithArguments(int a, int b) {
            return a + b + c;
        }

    }

}