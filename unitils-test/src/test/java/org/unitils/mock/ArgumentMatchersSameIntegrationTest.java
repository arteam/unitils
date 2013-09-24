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

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.mock.ArgumentMatchers.same;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersSameIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;

    private TestClass testClass1;
    private TestClass testClass2;


    @Before
    public void initialize() {
        testClass1 = new TestClass();
        testClass2 = new TestClass();
    }


    @Test
    public void matchWhenSame() {
        mockObject.returns("ok").method(same(testClass1));

        String result = mockObject.getMock().method(testClass1);
        assertEquals("ok", result);
        mockObject.assertInvoked().method(same(testClass1));
    }

    @Test
    public void noMatchWhenNotSame() {
        mockObject.returns("ok").method(same(testClass1));

        String result = mockObject.getMock().method(testClass2);
        assertNull(result);
        mockObject.assertNotInvoked().method(same(testClass1));
    }

    @Test
    public void noMatchWithNullActualValue() {
        mockObject.returns("ok").method(same(testClass1));

        String result = mockObject.getMock().method(null);
        assertNull(result);
        mockObject.assertNotInvoked().method(same(testClass1));
    }

    @Test
    public void noMatchWithNullExpectedValue() {
        mockObject.returns("ok").method(same((TestClass) null));

        String result = mockObject.getMock().method(testClass1);
        assertNull(result);
        mockObject.assertNotInvoked().method(same((TestClass) null));
    }

    @Test
    public void matchWithBothNull() {
        mockObject.returns("ok").method(same((TestClass) null));

        String result = mockObject.getMock().method(null);
        assertEquals("ok", result);
        mockObject.assertInvoked().method(same((TestClass) null));
    }

    @Test
    public void noMatchForPrimitiveValue() {
        mockObject.returns("ok").intMethod(same(5));

        String result = mockObject.getMock().intMethod(5);
        assertNull(result);
        mockObject.assertNotInvoked().intMethod(same(5));
    }

    @Test
    public void argumentsAreMatchedByReference() {
        List<String> list = new ArrayList<String>();
        list.add("1");
        mockObject.returns("ok").listMethod(same(list));

        list.add("2");
        String result = mockObject.getMock().listMethod(list);
        assertEquals("ok", result);

        list.add("3");
        mockObject.assertInvoked().listMethod(same(list));
        mockObject.assertNotInvoked().listMethod(same(asList("1", "2")));
    }

    @Test
    public void constructionForCoverage() {
        new ArgumentMatchers();
    }


    public static interface TestInterface {

        String method(TestClass value);

        String intMethod(int value);

        String listMethod(List<String> value);
    }

    public static class TestClass {
    }
}