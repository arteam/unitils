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
public class FieldWrapperSetValueTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field field;
    private Field primitiveField;
    private Field otherClassField;
    private Field staticField;
    private MyClass object;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        primitiveField = MyClass.class.getDeclaredField("primitiveField");
        otherClassField = OtherClass.class.getDeclaredField("field");
        staticField = MyClass.class.getDeclaredField("staticField");

        object = new MyClass();
        object.field = "original value";
        object.primitiveField = 555;
        MyClass.staticField = "original static value";

        fieldWrapper = new FieldWrapper(field);
    }


    @Test
    public void regularField() {
        OriginalFieldValue result = fieldWrapper.setValue("value", object);

        assertEquals("value", object.field);
        assertEquals("original value", result.getOriginalValue());
        assertEquals(field, result.getField());
        assertEquals(object, result.getObject());
    }

    @Test
    public void staticField() {
        fieldWrapper = new FieldWrapper(staticField);
        OriginalFieldValue result = fieldWrapper.setValue("value", null);

        assertEquals("value", MyClass.staticField);
        assertEquals("original static value", result.getOriginalValue());
        assertEquals(staticField, result.getField());
        assertNull(result.getObject());
    }

    @Test
    public void nullValue() {
        object.field = "value";

        fieldWrapper.setValue(null, object);
        assertNull(object.field);
    }

    @Test
    public void valueCanBeRestoredToOriginalValue() {
        OriginalFieldValue result = fieldWrapper.setValue("value", object);

        result.restoreToOriginalValue();
        assertEquals("original value", object.field);
    }

    @Test
    public void valueCanBeRestoredToNull() {
        OriginalFieldValue result = fieldWrapper.setValue("value", object);

        result.restoreToNullOr0();
        assertNull(object.field);
    }

    @Test
    public void valueCanBeRestoredTo0() {
        fieldWrapper = new FieldWrapper(primitiveField);
        OriginalFieldValue result = fieldWrapper.setValue(999, object);

        result.restoreToNullOr0();
        assertEquals(0, object.primitiveField);
    }

    @Test
    public void fieldDoesNotExist() {
        fieldWrapper = new FieldWrapper(otherClassField);
        try {
            fieldWrapper.setValue("value", object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for field with name 'field'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: value", e.getMessage());
        }
    }

    @Test
    public void wrongValueType() {
        fieldWrapper = new FieldWrapper(field);
        try {
            fieldWrapper.setValue(111L, object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for field with name 'field'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: 111", e.getMessage());
        }
    }

    @Test
    public void wrongStaticValueType() {
        fieldWrapper = new FieldWrapper(staticField);
        try {
            fieldWrapper.setValue(111L, null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for field with name 'staticField'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: 111", e.getMessage());
        }
    }

    @Test
    public void nullObject() {
        try {
            fieldWrapper.setValue("value", null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for field with name 'field'. Object cannot be null.", e.getMessage());
        }
    }


    public static class MyClass {

        private static String staticField;

        private String field;
        private int primitiveField;
    }

    public static class OtherClass {

        private String field;
    }
}
