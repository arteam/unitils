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
package org.unitils.easymock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.util.*;

import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.unitils.easymock.EasyMockUnitils.*;

/**
 * @author Tim Ducheyne
 */
public class EasyMockUnitilsCreateMockIntegrationTest extends UnitilsJUnit4 {


    @Test
    public void createMockWithDefaults() {
        MyInterface result = createMock(MyInterface.class);
        expect(result.method1("1")).andReturn("1");
        expect(result.method1("2")).andReturn("2");

        replay();
        result.method1("1");
        result.method1("2");
    }

    @Test
    public void orderIsIgnoredByDefault() {
        MyInterface result = createMock(MyInterface.class);
        expect(result.method1("1")).andReturn("1");
        expect(result.method1("2")).andReturn("2");

        replay();
        result.method1("2");
        result.method1("1");
    }

    @Test
    public void strictOrder() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.STRICT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.DEFAULT);
        expect(result.method1("1")).andReturn("1");
        expect(result.method1("2")).andReturn("2");

        replay();
        try {
            result.method1("2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method1(\"2\"):\n" +
                    "    method1(\"1\"): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void callsAreStrictByDefault() {
        MyInterface result = createMock(MyInterface.class);

        replay();
        try {
            result.method1("2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method1(\"2\"):", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void niceMockWhenLenientCalls() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.LENIENT, Order.DEFAULT, Dates.DEFAULT, Defaults.DEFAULT);

        replay();
        String value = result.method1("2");
        assertNull(value);
    }

    @Test
    public void strictMockWhenStrictCalls() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.STRICT, Order.DEFAULT, Dates.DEFAULT, Defaults.DEFAULT);

        replay();
        try {
            result.method1("2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method1(\"2\"):", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void listOrderIsLenientByDefault() {
        MyInterface result = createMock(MyInterface.class);
        expect(result.method2(asList("1", "2", "3"))).andReturn("1");

        replay();
        String value = result.method2(asList("3", "2", "1"));
        assertEquals("1", value);
    }

    @Test
    public void strictListOrder() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.STRICT, Dates.DEFAULT, Defaults.DEFAULT);
        expect(result.method2(asList("1", "2", "3"))).andReturn("1");

        replay();
        try {
            result.method2(asList("3", "2", "1"));
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method2([3, 2, 1]):\n" +
                    "    method2([1, 2, 3]): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void datesAreStrictByDefault() {
        MyInterface result = createMock(MyInterface.class);
        expect(result.method3(new Date(0))).andReturn("1");

        replay();
        try {
            result.method3(new Date(999));
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method3(Thu Jan 01 01:00:00 CET 1970):\n" +
                    "    method3(Thu Jan 01 01:00:00 CET 1970): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void lenientDates() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.LENIENT, Defaults.DEFAULT);
        expect(result.method3(new Date(0))).andReturn("1");

        replay();
        String value = result.method3(new Date(999));
        assertEquals("1", value);
    }

    @Test
    public void defaultArgumentValuesAreIgnoredByDefault() {
        MyInterface result = createMock(MyInterface.class);
        expect(result.method1(null)).andReturn("1");

        replay();
        String value = result.method1("value");
        assertEquals("1", value);
    }

    @Test
    public void defaultInnerArgumentValuesAreIgnoredByDefault() {
        MyInterface result = createMock(MyInterface.class);
        expect(result.method4(new MyClass(null))).andReturn("1");

        replay();
        String value = result.method4(new MyClass("value"));
        assertEquals("1", value);
    }

    @Test
    public void strictDefaultValues() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.STRICT);
        expect(result.method1(null)).andReturn("1");

        replay();
        try {
            result.method1("value");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method1(\"value\"):\n" +
                    "    method1(null): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void strictInnerDefaultValues() {
        MyInterface result = createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.STRICT);
        expect(result.method4(new MyClass(null))).andReturn("1");

        replay();
        try {
            result.method4(new MyClass("value"));
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method4(MyClass(value)):\n" +
                    "    method4(MyClass(null)): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }


    public static interface MyInterface {

        String method1(String arg);

        String method2(List<String> arg);

        String method3(Date arg);

        String method4(MyClass arg);
    }

    public static class MyClass {

        String value;

        public MyClass(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "MyClass(" + value + ")";
        }
    }
}