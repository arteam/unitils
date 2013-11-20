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
import org.unitils.mock.argumentmatcher.Capture;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.get;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersGetIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void captureWhenDefiningBehavior() {
        TestClass argument = new TestClass();
        Capture<TestClass> capture = new Capture<TestClass>(TestClass.class);
        mockObject.returns("ok").method(get(capture));

        String result = mockObject.getMock().method(argument);
        argument.value = "xxx";
        assertEquals("ok", result);
        assertSame(argument, capture.getValue());
        assertEquals("xxx", capture.getValue().value);
        assertEquals("original", capture.getValueAtInvocationTime().value);
    }

    @Test
    public void captureWhenAsserting() {
        TestClass argument = new TestClass();
        Capture<TestClass> capture = new Capture<TestClass>(TestClass.class);

        String result = mockObject.getMock().method(argument);
        argument.value = "xxx";
        mockObject.assertInvoked().method(get(capture));
        assertSame(argument, capture.getValue());
        assertEquals("xxx", capture.getValue().value);
        assertEquals("original", capture.getValueAtInvocationTime().value);
    }

    @Test
    public void lastCaptureOverridesPreviousCaptures() {
        TestClass argument1 = new TestClass();
        TestClass argument2 = new TestClass();
        Capture<TestClass> capture = new Capture<TestClass>(TestClass.class);

        mockObject.getMock().method(argument1);
        mockObject.getMock().method(argument2);
        mockObject.assertInvoked().method(get(capture));
        mockObject.assertInvoked().method(get(capture));
        assertSame(argument2, capture.getValue());
    }

    @Test
    public void matchWhenSubType() {
        Capture<TestClass> capture = new Capture<TestClass>(TestClass.class);
        mockObject.returns("ok").method(get(capture));

        String result = mockObject.getMock().method(new SubClass());
        assertEquals("ok", result);
        mockObject.assertInvoked().method(get(capture));
    }

    @Test
    public void noMatchWhenSuperType() {
        Capture<SubClass> capture = new Capture<SubClass>(SubClass.class);
        mockObject.returns("ok").method(get(capture));

        String result = mockObject.getMock().method(new TestClass());
        assertNull(result);
        mockObject.assertNotInvoked().method(get(capture));
    }

    @Test
    public void noMatchForNull() {
        Capture<TestClass> capture = new Capture<TestClass>(TestClass.class);
        mockObject.returns("ok").method(get(capture));

        String result = mockObject.getMock().method(null);
        assertNull(result);
        mockObject.assertNotInvoked().method(get(capture));
    }

    @Test
    public void exceptionWhenPrimitiveValue() {
        Capture<Integer> capture = new Capture<Integer>(Integer.class);
        try {
            mockObject.returns("ok").intMethod(get(capture));
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
        public String value = "original";
    }

    public static class SubClass extends TestClass {
    }
}