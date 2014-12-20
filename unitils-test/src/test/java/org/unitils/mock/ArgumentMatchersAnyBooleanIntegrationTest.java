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

import static java.lang.Boolean.FALSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.unitils.mock.ArgumentMatchers.anyBoolean;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersAnyBooleanIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void primitiveTrue() {
        mockObject.returns("ok").primitive(anyBoolean());

        String result = mockObject.getMock().primitive(true);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyBoolean());
    }

    @Test
    public void primitiveFalse() {
        mockObject.returns("ok").primitive(anyBoolean());

        String result = mockObject.getMock().primitive(false);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyBoolean());
    }

    @Test
    public void wrapper() {
        mockObject.returns("ok").wrapper(anyBoolean());

        String result = mockObject.getMock().wrapper(FALSE);
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyBoolean());
    }

    @Test
    public void autoUnBoxed() {
        mockObject.returns("ok").primitive(anyBoolean());

        String result = mockObject.getMock().primitive(FALSE);
        assertEquals("ok", result);
        mockObject.assertInvoked().primitive(anyBoolean());
    }

    @Test
    public void autoBoxed() {
        mockObject.returns("ok").wrapper(anyBoolean());

        String result = mockObject.getMock().wrapper(false);
        assertEquals("ok", result);
        mockObject.assertInvoked().wrapper(anyBoolean());
    }

    @Test
    public void noMatchWhenNull() {
        mockObject.returns("ok").wrapper(anyBoolean());

        String result = mockObject.getMock().wrapper(null);
        assertNull(result);
        mockObject.assertNotInvoked().wrapper(anyBoolean());
    }


    public static interface TestInterface {

        String primitive(boolean arg1);

        String wrapper(Boolean arg1);
    }
}