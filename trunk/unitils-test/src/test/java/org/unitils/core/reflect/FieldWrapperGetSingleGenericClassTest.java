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
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperGetSingleGenericClassTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field regularField;
    private Field genericField;
    private Field multiGenericField;
    private Field nestedGenericField;


    @Before
    public void initialize() throws Exception {
        regularField = MyClass.class.getDeclaredField("regularField");
        genericField = MyClass.class.getDeclaredField("genericField");
        multiGenericField = MyClass.class.getDeclaredField("multiGenericField");
        nestedGenericField = MyClass.class.getDeclaredField("nestedGenericField");
    }


    @Test
    public void genericField() {
        fieldWrapper = new FieldWrapper(genericField);

        Class<?> result = fieldWrapper.getSingleGenericClass();
        assertEquals(String.class, result);
    }

    @Test
    public void exceptionWhenNoGenericType() {
        try {
            fieldWrapper = new FieldWrapper(regularField);
            fieldWrapper.getSingleGenericClass();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic type for type: java.lang.String.\n" +
                    "Type is not a generic type.", e.getMessage());
        }
    }

    @Test
    public void rawTypeWhenNestedGenericType() {
        fieldWrapper = new FieldWrapper(nestedGenericField);

        Class<?> result = fieldWrapper.getSingleGenericClass();
        assertEquals(List.class, result);
    }

    @Test
    public void exceptionWhenMoreThanOneGenericType() {
        try {
            fieldWrapper = new FieldWrapper(multiGenericField);
            fieldWrapper.getSingleGenericClass();
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic type for type: java.util.Map<java.lang.String, java.lang.String>.\n" +
                    "The type declares more than one generic type: [class java.lang.String, class java.lang.String]", e.getMessage());
        }
    }


    private static class MyClass {

        private String regularField;
        private List<String> genericField;
        private Map<String, String> multiGenericField;
        private List<List<String>> nestedGenericField;
    }
}
