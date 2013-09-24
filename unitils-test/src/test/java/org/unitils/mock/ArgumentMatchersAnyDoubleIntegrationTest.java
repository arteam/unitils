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
import static org.unitils.mock.ArgumentMatchers.anyDouble;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersAnyDoubleIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void primitive() {
        mockObject.returns("ok").primitive(anyDouble());

        String result = mockObject.getMock().primitive(5D);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyDouble());
    }

    @Test
    public void wrapper() {
        mockObject.returns("ok").wrapper(anyDouble());

        String result = mockObject.getMock().wrapper(new Double("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyDouble());
    }

    @Test
    public void autoUnBoxed() {
        mockObject.returns("ok").primitive(anyDouble());

        String result = mockObject.getMock().primitive(new Double("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyDouble());
    }

    @Test
    public void autoBoxed() {
        mockObject.returns("ok").wrapper(anyDouble());

        String result = mockObject.getMock().wrapper(5D);
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyDouble());
    }

    @Test
    public void noMatchWhenNull() {
        mockObject.returns("ok").wrapper(anyDouble());

        String result = mockObject.getMock().wrapper(null);
        assertNull(result);
        mockObject.assertNotInvoked().wrapper(anyDouble());
    }


    public static interface TestInterface {

        String primitive(double arg1);

        String wrapper(Double arg1);
    }
}