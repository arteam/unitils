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
import static org.unitils.mock.ArgumentMatchers.anyInt;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersAnyIntIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void primitive() {
        mockObject.returns("ok").primitive(anyInt());

        String result = mockObject.getMock().primitive(5);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyInt());
    }

    @Test
    public void wrapper() {
        mockObject.returns("ok").wrapper(anyInt());

        String result = mockObject.getMock().wrapper(new Integer("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyInt());
    }

    @Test
    public void autoUnBoxed() {
        mockObject.returns("ok").primitive(anyInt());

        String result = mockObject.getMock().primitive(new Integer("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyInt());
    }

    @Test
    public void autoBoxed() {
        mockObject.returns("ok").wrapper(anyInt());

        String result = mockObject.getMock().wrapper(5);
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyInt());
    }

    @Test
    public void noMatchWhenNull() {
        mockObject.returns("ok").wrapper(anyInt());

        String result = mockObject.getMock().wrapper(null);
        assertNull(result);
        mockObject.assertNotInvoked().wrapper(anyInt());
    }

    @Test
    public void notAnInt() {
        mockObject.returns("ok").doubleMethod(anyInt());

        String result = mockObject.getMock().doubleMethod(5.99);
        assertNull(result);
        mockObject.assertNotInvoked().doubleMethod(anyInt());
    }


    public static interface TestInterface {

        String primitive(int arg1);

        String wrapper(Integer arg1);

        String doubleMethod(double arg1);
    }
}