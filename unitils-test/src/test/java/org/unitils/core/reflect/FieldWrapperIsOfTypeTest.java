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
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperIsOfTypeTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field objectField;
    private Field stringField;
    private Field stringListField;
    private Field stringBuilderListField;


    @Before
    public void initialize() throws Exception {
        objectField = MyClass.class.getDeclaredField("objectField");
        stringField = MyClass.class.getDeclaredField("stringField");
        stringListField = MyClass.class.getDeclaredField("stringListField");
        stringBuilderListField = MyClass.class.getDeclaredField("stringBuilderListField");
    }


    @Test
    public void classType() {
        fieldWrapper = new FieldWrapper(stringField);

        boolean result = fieldWrapper.isOfType(String.class);
        assertTrue(result);
    }

    @Test
    public void notOfClassType() {
        fieldWrapper = new FieldWrapper(stringField);

        boolean result = fieldWrapper.isOfType(StringBuilder.class);
        assertFalse(result);
    }

    @Test
    public void assignableTypeIsNotOfType() {
        fieldWrapper = new FieldWrapper(objectField);

        boolean result = fieldWrapper.isOfType(String.class);
        assertFalse(result);
    }

    @Test
    public void genericType() throws Exception {
        Type genericType = stringListField.getGenericType();
        fieldWrapper = new FieldWrapper(stringListField);

        boolean result = fieldWrapper.isOfType(genericType);
        assertTrue(result);
    }

    @Test
    public void notOfGenericType() throws Exception {
        Type genericType = stringBuilderListField.getGenericType();
        fieldWrapper = new FieldWrapper(stringListField);

        boolean result = fieldWrapper.isOfType(genericType);
        assertFalse(result);
    }


    public static class MyClass {

        private Object objectField;
        private String stringField;
        private List<String> stringListField;
        private List<StringBuilder> stringBuilderListField;
    }
}
