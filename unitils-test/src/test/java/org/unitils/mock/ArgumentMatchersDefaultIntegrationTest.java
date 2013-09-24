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

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersDefaultIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void matchWhenEqual() {
        mockObject.returns("ok").method(new TestClass("value"));

        String result = mockObject.getMock().method(new TestClass("value"));
        assertEquals("ok", result);
        mockObject.assertInvoked().method(new TestClass("value"));
    }

    @Test
    public void noMatchWhenNotEqual() {
        mockObject.returns("ok").method(new TestClass("value"));

        String result = mockObject.getMock().method(new TestClass("xxx"));
        assertNull(result);
        mockObject.assertNotInvoked().method(new TestClass("value"));
    }

    @Test
    public void noMatchWithNullActualValue() {
        mockObject.returns("ok").method(new TestClass("value"));

        String result = mockObject.getMock().method(null);
        assertNull(result);
        mockObject.assertNotInvoked().method(new TestClass("value"));
    }

    @Test
    public void alwaysMatchWhenNullExpectedValue() {
        mockObject.returns("ok").method(null);

        String result = mockObject.getMock().method(new TestClass("value"));
        assertEquals("ok", result);
        mockObject.assertInvoked().method(null);
    }

    @Test
    public void matchWithBothNull() {
        mockObject.returns("ok").method(null);

        String result = mockObject.getMock().method(null);
        assertEquals("ok", result);
        mockObject.assertInvoked().method(null);
    }

    @Test
    public void lenientOrder() {
        mockObject.returns("ok").listMethod(asList("1", "2"));

        String result = mockObject.getMock().listMethod(asList("2", "1"));
        assertEquals("ok", result);
        mockObject.assertInvoked().listMethod(asList("1", "2"));
    }

    @Test
    public void lenientDefaults() {
        mockObject.returns("ok").method(new TestClass(null));

        String result = mockObject.getMock().method(new TestClass("value"));
        assertEquals("ok", result);
        mockObject.assertInvoked().method(new TestClass(null));
    }

    @Test
    public void primitiveValue() {
        mockObject.returns("ok").intMethod(5);

        String result = mockObject.getMock().intMethod(5);
        assertEquals("ok", result);
        mockObject.assertInvoked().intMethod(5);
    }

    @Test
    public void zeroPrimitiveValueIsNotIgnored() {
        mockObject.returns("ok").intMethod(0);

        String result = mockObject.getMock().intMethod(5);
        assertNull(result);
        mockObject.assertNotInvoked().intMethod(0);
    }

    @Test
    public void falsePrimitiveValueIsNotIgnored() {
        mockObject.returns("ok").booleanMethod(false);

        String result = mockObject.getMock().booleanMethod(true);
        assertNull(result);
        mockObject.assertNotInvoked().booleanMethod(false);
    }

    @Test
    public void argumentsAreMatchedByReference() {
        List<String> list = new ArrayList<String>();
        list.add("1");
        mockObject.returns("ok").listMethod(list);

        list.add("2");
        String result = mockObject.getMock().listMethod(list);
        assertEquals("ok", result);

        list.add("3");
        mockObject.assertInvoked().listMethod(list);
    }


    public static interface TestInterface {

        String method(TestClass value);

        String listMethod(List<String> value);

        String intMethod(int value);

        String booleanMethod(boolean value);
    }

    public static class TestClass {

        private String value;

        public TestClass(String value) {
            this.value = value;
        }
    }
}