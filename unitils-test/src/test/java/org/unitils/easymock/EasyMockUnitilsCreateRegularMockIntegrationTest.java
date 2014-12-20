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
import org.unitils.easymock.util.Calls;
import org.unitils.easymock.util.InvocationOrder;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;
import static org.unitils.easymock.EasyMockUnitils.*;

/**
 * @author Tim Ducheyne
 */
public class EasyMockUnitilsCreateRegularMockIntegrationTest extends UnitilsJUnit4 {


    @Test
    public void createRegularMockWithDefaults() {
        MyInterface result = createRegularMock(MyInterface.class);
        expect(result.method("1")).andReturn("1");
        expect(result.method("2")).andReturn("2");

        replay();
        result.method("1");
        result.method("2");
    }

    @Test
    public void orderIsIgnoredByDefault() {
        MyInterface result = createRegularMock(MyInterface.class);
        expect(result.method("1")).andReturn("1");
        expect(result.method("2")).andReturn("2");

        replay();
        result.method("2");
        result.method("1");
    }

    @Test
    public void strictOrder() {
        MyInterface result = createRegularMock(MyInterface.class, InvocationOrder.STRICT, Calls.DEFAULT);
        expect(result.method("1")).andReturn("1");
        expect(result.method("2")).andReturn("2");

        replay();
        try {
            result.method("2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method(\"2\"):\n" +
                    "    method(\"1\"): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void callsAreStrictByDefault() {
        MyInterface result = createRegularMock(MyInterface.class);
        expect(result.method(null)).andReturn("1");

        replay();
        try {
            result.method("2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method(\"2\"):\n" +
                    "    method(null): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void niceMockWhenLenientCalls() {
        MyInterface result = createRegularMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.LENIENT);

        replay();
        String value = result.method("2");
        assertNull(value);
    }

    @Test
    public void strictMockWhenStrictCalls() {
        MyInterface result = createRegularMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.STRICT);

        replay();
        try {
            result.method("2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method(\"2\"):", e.getMessage());
            clearMocks();
        }
    }


    public static interface MyInterface {

        String method(String arg);
    }
}