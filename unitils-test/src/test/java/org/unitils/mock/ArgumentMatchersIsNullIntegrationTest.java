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
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class ArgumentMatchersIsNullIntegrationTest extends UnitilsJUnit4 {

    /* Test mock object */
    private Mock<TestInterface> mockObject;


    @Test
    public void matchWhenNull() {
        mockObject.returns("ok").method(isNull(String.class));

        String result = mockObject.getMock().method(null);
        assertEquals("ok", result);
        mockObject.assertInvoked().method(isNull(String.class));
    }

    @Test
    public void noMatchWhenNotNull() {
        mockObject.returns("ok").method(isNull(String.class));

        String result = mockObject.getMock().method("value");
        assertNull(result);
        mockObject.assertNotInvoked().method(isNull(String.class));
    }

    @Test(expected = NullPointerException.class)
    public void nullPointerForPrimitiveValue() {
        mockObject.returns("ok").intMethod(isNull(Integer.class));
    }


    public static interface TestInterface {

        String method(String value);

        String intMethod(int value);
    }
}