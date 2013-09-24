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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.mock.ArgumentMatchers.anyLong;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersAnyLongIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void primitive() {
        mockObject.returns("ok").primitive(anyLong());

        String result = mockObject.getMock().primitive(5L);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyLong());
    }

    @Test
    public void wrapper() {
        mockObject.returns("ok").wrapper(anyLong());

        String result = mockObject.getMock().wrapper(new Long("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyLong());
    }

    @Test
    public void autoUnBoxed() {
        mockObject.returns("ok").primitive(anyLong());

        String result = mockObject.getMock().primitive(new Long("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyLong());
    }

    @Test
    public void autoBoxed() {
        mockObject.returns("ok").wrapper(anyLong());

        String result = mockObject.getMock().wrapper(5L);
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyLong());
    }

    @Test
    public void noMatchWhenNull() {
        mockObject.returns("ok").wrapper(anyLong());

        String result = mockObject.getMock().wrapper(null);
        assertNull(result);
        mockObject.assertNotInvoked().wrapper(anyLong());
    }

    @Test
    public void notALong() {
        mockObject.returns("ok").doubleMethod(anyLong());

        String result = mockObject.getMock().doubleMethod(5.99);
        assertNull(result);
        mockObject.assertNotInvoked().doubleMethod(anyLong());
    }


    public static interface TestInterface {

        String primitive(long arg1);

        String wrapper(Long arg1);

        String doubleMethod(double arg1);
    }
}