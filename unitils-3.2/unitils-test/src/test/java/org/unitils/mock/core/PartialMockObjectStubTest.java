/*
 *
 *  * Copyright 2010,  Unitils.org
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package org.unitils.mock.core;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the mock object functionality for partial mocks that wrap around an existing instance.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PartialMockObjectStubTest {

    /* Class under test */
    private PartialMockObject<TestClass> mockObject;


    @Before
    public void setUp() {
        mockObject = new PartialMockObject<TestClass>(TestClass.class, this);
        TestClass.invoked = false;
    }


    @Test
    public void stubMethod() {
        mockObject.stub().method();

        mockObject.getMock().method();
        assertFalse(TestClass.invoked);
        mockObject.assertInvoked().method();
    }

    @Test
    public void stubReturnsDefaultValue() {
        mockObject.stub().methodWithReturnValue();

        List<String> result = mockObject.getMock().methodWithReturnValue();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertFalse(TestClass.invoked);
        mockObject.assertInvoked().methodWithReturnValue();

    }

    @Test
    public void stubMethodCalledFromOtherMethod() {
        mockObject.stub().method();

        mockObject.getMock().methodThatCallsOtherMethod();
        assertFalse(TestClass.invoked);
        mockObject.assertInvoked().method();
        mockObject.assertInvoked().methodThatCallsOtherMethod();
    }


    public static class TestClass {

        public static boolean invoked;

        public void methodThatCallsOtherMethod() {
            method();
        }

        protected void method() {
            invoked = true;
        }

        protected List<String> methodWithReturnValue() {
            invoked = true;
            return null;
        }

    }

}