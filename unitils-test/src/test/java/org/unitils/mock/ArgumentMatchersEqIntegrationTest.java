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
import static org.unitils.mock.ArgumentMatchers.eq;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersEqIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void matchWhenEqual() {
        mockObject.returns("ok").method(eq(asList("value")));

        String result = mockObject.getMock().method(asList("value"));
        assertEquals("ok", result);
        mockObject.assertInvoked().method(eq(asList("value")));
    }

    @Test
    public void noMatchWhenNotEqual() {
        mockObject.returns("ok").method(eq(asList("value")));

        String result = mockObject.getMock().method(asList("xxx"));
        assertNull(result);
        mockObject.assertNotInvoked().method(eq(asList("value")));
    }

    @Test
    public void noMatchWithNullActualValue() {
        mockObject.returns("ok").method(eq(asList("value")));

        String result = mockObject.getMock().method(null);
        assertNull(result);
        mockObject.assertNotInvoked().method(eq(asList("value")));
    }

    @Test
    public void noMatchWithNullExpectedValue() {
        mockObject.returns("ok").method(eq((List<String>) null));

        String result = mockObject.getMock().method(asList("value"));
        assertNull(result);
        mockObject.assertNotInvoked().method(eq((List<String>) null));
    }

    @Test
    public void matchWithBothNull() {
        mockObject.returns("ok").method(eq((List<String>) null));

        String result = mockObject.getMock().method(null);
        assertEquals("ok", result);
        mockObject.assertInvoked().method(eq((List<String>) null));
    }

    @Test
    public void primitiveValue() {
        mockObject.returns("ok").intMethod(eq(5));

        String result = mockObject.getMock().intMethod(5);
        assertEquals("ok", result);
        mockObject.assertInvoked().intMethod(eq(5));
    }

    /**
     * Tests the equals argument matcher in case the object changes between the behavior definition,
     * the actual method call and the assert statement. Since the eq() argument matcher uses the original
     * object reference and not a copy of the object, the values should keep on matching.
     */
    @Test
    public void eqMatcherUsesOriginalObjectNotACopy() {
        List<String> list = new ArrayList<String>();
        mockObject.returns("ok").method(eq(list));
        list.add("test");

        String result = mockObject.getMock().method(list);
        assertEquals("ok", result);

        list.add("test");
        mockObject.assertInvoked().method(eq(list));
    }


    public static interface TestInterface {

        String method(List<String> value);

        String intMethod(int value);
    }
}