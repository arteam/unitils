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
import static org.unitils.mock.ArgumentMatchers.anyByte;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersAnyByteIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;


    @Test
    public void primitive() {
        mockObject.returns("ok").primitive(anyByte());

        String result = mockObject.getMock().primitive((byte) 5);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyByte());
    }

    @Test
    public void wrapper() {
        mockObject.returns("ok").wrapper(anyByte());

        String result = mockObject.getMock().wrapper(new Byte("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyByte());
    }

    @Test
    public void autoUnBoxed() {
        mockObject.returns("ok").primitive(anyByte());

        String result = mockObject.getMock().primitive(new Byte("5"));
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyByte());
    }

    @Test
    public void autoBoxed() {
        mockObject.returns("ok").wrapper(anyByte());

        String result = mockObject.getMock().wrapper((byte) 5);
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyByte());
    }

    @Test
    public void noMatchWhenNull() {
        mockObject.returns("ok").wrapper(anyByte());

        String result = mockObject.getMock().wrapper(null);
        assertNull(result);
        mockObject.assertNotInvoked().wrapper(anyByte());
    }

    @Test
    public void notAByte() {
        mockObject.returns("ok").doubleMethod(anyByte());

        String result = mockObject.getMock().doubleMethod(5.99);
        assertNull(result);
        mockObject.assertNotInvoked().doubleMethod(anyByte());
    }


    public static interface TestInterface {

        String primitive(byte arg1);

        String wrapper(Byte arg1);

        String doubleMethod(double arg1);
    }
}