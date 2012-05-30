/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.core.reflect;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperGetValueTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field field;
    private Field otherClassField;
    private Field staticField;
    private MyClass object;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        otherClassField = OtherClass.class.getDeclaredField("field");
        staticField = MyClass.class.getDeclaredField("staticField");

        object = new MyClass();
        object.field = "value";
        MyClass.staticField = "static value";

        fieldWrapper = new FieldWrapper(field);
    }


    @Test
    public void regularField() {
        String result = fieldWrapper.getValue(object);
        assertEquals("value", result);
    }

    @Test
    public void staticField() {
        fieldWrapper = new FieldWrapper(staticField);

        String result = fieldWrapper.getValue(null);
        assertEquals("static value", MyClass.staticField);
    }

    @Test
    public void nullValue() {
        object.field = null;

        String result = fieldWrapper.getValue(object);
        assertNull(result);
    }

    @Test
    public void fieldDoesNotExist() {
        fieldWrapper = new FieldWrapper(otherClassField);
        try {
            fieldWrapper.getValue(object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value of field with name 'field'.\n" +
                    "Make sure that the field exists on the target object.\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitilsnew.core.reflect.FieldWrapperGetValueTest$OtherClass.field to org.unitilsnew.core.reflect.FieldWrapperGetValueTest$MyClass", e.getMessage());
        }
    }

    @Test(expected = ClassCastException.class)
    public void wrongValueType() {
        fieldWrapper = new FieldWrapper(field);
        Long result = fieldWrapper.getValue(object);
    }

    @Test
    public void nullObject() {
        try {
            fieldWrapper.getValue(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value of field with name 'field'. Object cannot be null.", e.getMessage());
        }
    }


    public static class MyClass {

        private static String staticField;

        private String field;
    }

    public static class OtherClass {

        private String field;
    }
}
