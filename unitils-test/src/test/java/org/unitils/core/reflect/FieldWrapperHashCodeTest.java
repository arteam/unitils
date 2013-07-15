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

package org.unitils.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperHashCodeTest {

    private Field field;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
    }


    @Test
    public void hashCodeForField() {
        FieldWrapper fieldWrapper = new FieldWrapper(field);
        int result = fieldWrapper.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        FieldWrapper fieldWrapper1 = new FieldWrapper(field);
        FieldWrapper fieldWrapper2 = new FieldWrapper(field);

        assertEquals(fieldWrapper1.hashCode(), fieldWrapper2.hashCode());
    }

    @Test
    public void nullField() {
        FieldWrapper fieldWrapper = new FieldWrapper(null);
        int result = fieldWrapper.hashCode();

        assertEquals(0, result);
    }


    private static class MyClass {

        private String field;
    }
}
