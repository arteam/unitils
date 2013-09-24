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

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.any;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersAnyIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void matchWhenCorrectType() {
        mockObject.returns("ok").method(any(TestClass.class));

        String result = mockObject.getMock().method(new TestClass());
        assertEquals("ok", result);
        mockObject.assertInvoked().method(any(TestClass.class));
    }

    @Test
    public void matchWhenSubType() {
        mockObject.returns("ok").method(any(TestClass.class));

        String result = mockObject.getMock().method(new SubClass());
        assertEquals("ok", result);
        mockObject.assertInvoked().method(any(TestClass.class));
    }

    @Test
    public void noMatchWhenSuperType() {
        mockObject.returns("ok").method(any(SubClass.class));

        String result = mockObject.getMock().method(new TestClass());
        assertNull(result);
        mockObject.assertNotInvoked().method(any(SubClass.class));
    }

    @Test
    public void noMatchForNull() {
        mockObject.returns(true).method(any(TestClass.class));

        String result = mockObject.getMock().method(null);
        assertNull(result);
        mockObject.assertNotInvoked().method(any(TestClass.class));
    }

    @Test
    public void noMatchWhenNullClass() {
        mockObject.returns(true).method(any((Class<TestClass>) null));

        String result = mockObject.getMock().method(new TestClass());
        assertNull(result);
        mockObject.assertNotInvoked().method(any((Class<TestClass>) null));
    }

    @Test
    public void exceptionWhenPrimitiveValue() {
        try {
            mockObject.returns("ok").intMethod(any(Integer.class));
            fail("NullPointerException expected");
        } catch (NullPointerException e) {
            assertNull(e.getMessage());
        }
    }


    public static interface TestInterface {

        String method(TestClass value);

        String intMethod(int value);
    }

    public static class TestClass {
    }

    public static class SubClass extends TestClass {
    }
}