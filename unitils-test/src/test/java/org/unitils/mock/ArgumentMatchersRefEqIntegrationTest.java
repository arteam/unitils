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
import static org.unitils.mock.ArgumentMatchers.refEq;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersRefEqIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void matchWhenEqual() {
        mockObject.returns("ok").method(refEq(new TestClass("value")));

        String result = mockObject.getMock().method(new TestClass("value"));
        assertEquals("ok", result);
        mockObject.assertInvoked().method(refEq(new TestClass("value")));
    }

    @Test
    public void noMatchWhenNotEqual() {
        mockObject.returns("ok").method(refEq(new TestClass("value")));

        String result = mockObject.getMock().method(new TestClass("xxx"));
        assertNull(result);
        mockObject.assertNotInvoked().method(refEq(new TestClass("value")));
    }

    @Test
    public void noMatchWithNullActualValue() {
        mockObject.returns("ok").method(refEq(new TestClass("value")));

        String result = mockObject.getMock().method(null);
        assertNull(result);
        mockObject.assertNotInvoked().method(refEq(new TestClass("value")));
    }

    @Test
    public void noMatchWithNullExpectedValue() {
        mockObject.returns("ok").method(refEq((TestClass) null));

        String result = mockObject.getMock().method(new TestClass("value"));
        assertNull(result);
        mockObject.assertNotInvoked().method(refEq((TestClass) null));
    }

    @Test
    public void matchWithBothNull() {
        mockObject.returns("ok").method(refEq((TestClass) null));

        String result = mockObject.getMock().method(null);
        assertEquals("ok", result);
        mockObject.assertInvoked().method(refEq((TestClass) null));
    }

    @Test
    public void notLenient() {
        mockObject.returns("ok").listMethod(refEq(asList("1", "2")));

        String result = mockObject.getMock().listMethod(asList("2", "1"));
        assertNull(result);
        mockObject.assertNotInvoked().listMethod(refEq(asList("1", "2")));
    }

    @Test
    public void primitiveValue() {
        mockObject.returns("ok").intMethod(refEq(5));

        String result = mockObject.getMock().intMethod(5);
        assertEquals("ok", result);
        mockObject.assertInvoked().intMethod(refEq(5));
    }

    @Test
    public void argumentsAreMatchedByValue() {
        List<String> list = new ArrayList<String>();
        list.add("1");
        mockObject.returns("ok").listMethod(refEq(list));

        list.add("2");
        String result = mockObject.getMock().listMethod(list);
        assertNull(result);

        list.add("3");
        mockObject.assertNotInvoked().listMethod(refEq(list));
        mockObject.assertInvoked().listMethod(refEq(asList("1", "2")));
    }


    public static interface TestInterface {

        String method(TestClass value);

        String listMethod(List<String> value);

        String intMethod(int value);
    }

    public static class TestClass {

        private String value;

        public TestClass(String value) {
            this.value = value;
        }
    }
}