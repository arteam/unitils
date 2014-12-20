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
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field field;
    private Field genericField;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        genericField = MyClass.class.getDeclaredField("genericField");

        fieldWrapper = new FieldWrapper(field);
    }


    @Test
    public void getWrappedField() {
        Field result = fieldWrapper.getWrappedField();
        assertSame(field, result);
    }

    @Test
    public void getName() {
        String result = fieldWrapper.getName();
        assertEquals("field", result);
    }

    @Test
    public void getType() {
        Class<?> result = fieldWrapper.getType();
        assertEquals(String.class, result);
    }

    @Test
    public void getClassWrapper() {
        ClassWrapper result = fieldWrapper.getClassWrapper();
        assertEquals(String.class, result.getWrappedClass());
    }

    @Test
    public void nameAsToString() {
        String result = fieldWrapper.toString();
        assertEquals("field", result);
    }


    public static class MyClass {

        private String field;
        private List<String> genericField;
    }
}
