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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class CompositeFieldWrapperSetValueTest {

    /* Tested object */
    private CompositeFieldWrapper compositeFieldWrapper;

    private Field myClassField;
    private Field myClassInner1Field;
    private Field inner1Field;
    private Field inner1Inner2Field;
    private Field inner2Field;
    private Field inner2Inner3Field;
    private Field inner3Field;
    private Field otherClassField;
    private MyClass object;


    @Before
    public void initialize() throws Exception {
        myClassField = MyClass.class.getDeclaredField("field");
        myClassInner1Field = MyClass.class.getDeclaredField("inner1");
        inner1Field = Inner1.class.getDeclaredField("field");
        inner1Inner2Field = Inner1.class.getDeclaredField("inner2");
        inner2Field = Inner2.class.getDeclaredField("field");
        inner2Inner3Field = Inner2.class.getDeclaredField("inner3");
        inner3Field = Inner3.class.getDeclaredField("field");
        otherClassField = OtherClass.class.getDeclaredField("field");

        object = new MyClass();
        object.field = "original value";
        object.inner1 = new Inner1();
        object.inner1.field = "original inner1Value";
        object.inner1.inner2 = new Inner2();
        object.inner1.inner2.field = "original inner2Value";
    }


    @Test
    public void simpleField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassField));
        OriginalFieldValue result = compositeFieldWrapper.setValue("value", object);

        assertEquals("value", object.field);
        assertEquals("original value", result.getOriginalValue());
        assertEquals(myClassField, result.getFieldWrapper().getWrappedField());
        assertEquals(object, result.getObject());
    }

    @Test
    public void simpleFieldNullValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassField));
        object.field = null;

        compositeFieldWrapper.setValue(null, object);
        assertNull(object.field);
    }

    @Test
    public void oneLevelCompositeField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));
        OriginalFieldValue result = compositeFieldWrapper.setValue("inner1Value", object);

        assertEquals("inner1Value", object.inner1.field);
        assertEquals("original inner1Value", result.getOriginalValue());
        assertEquals(inner1Field, result.getFieldWrapper().getWrappedField());
        assertEquals(object.inner1, result.getObject());
    }

    @Test
    public void twoLevelCompositeField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Inner2Field, inner2Field));
        OriginalFieldValue result = compositeFieldWrapper.setValue("inner2Value", object);

        assertEquals("inner2Value", object.inner1.inner2.field);
        assertEquals("original inner2Value", result.getOriginalValue());
        assertEquals(inner2Field, result.getFieldWrapper().getWrappedField());
        assertEquals(object.inner1.inner2, result.getObject());
    }

    @Test
    public void simpleFieldValueCanBeRestoredToOriginalValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassField));
        OriginalFieldValue result = compositeFieldWrapper.setValue("value", object);

        result.restoreToOriginalValue();
        assertEquals("original value", object.field);
    }

    @Test
    public void compositeFieldValueCanBeRestoredToOriginalValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Inner2Field, inner2Field));
        OriginalFieldValue result = compositeFieldWrapper.setValue("inner2Value", object);

        result.restoreToOriginalValue();
        assertEquals("original inner2Value", object.inner1.inner2.field);
    }

    @Test
    public void innerFieldNullValueAndNoAutoCreate() {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));
        object.inner1 = null;
        try {
            compositeFieldWrapper.setValue("value", object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for composite field with name 'inner1.field'. Inner field with name 'inner1' is null.", e.getMessage());
        }
    }

    @Test
    public void autoCreatingInnerFieldWithNullValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));
        object.inner1 = null;

        compositeFieldWrapper.setValue("value", object, true);
        assertEquals("value", object.inner1.field);
    }

    @Test
    public void autoCreatingTwoInnerFieldsWithNullValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Inner2Field, inner2Field));
        object.inner1 = null;

        compositeFieldWrapper.setValue("value", object, true);
        assertEquals("value", object.inner1.inner2.field);
    }

    @Test
    public void firstAutoCreatedFieldCanBeRestoredToNullAsOriginalValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Inner2Field, inner2Field));
        object.inner1 = null;

        OriginalFieldValue result = compositeFieldWrapper.setValue("value", object, true);
        result.restoreToOriginalValue();
        assertNull(object.inner1);
    }

    @Test
    public void autoCreatingForClassWithoutDefaultConstructor() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Inner2Field, inner2Inner3Field, inner3Field));
        try {
            compositeFieldWrapper.setValue("value", object, true);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for composite field with name 'inner1.inner2.inner3.field'. Could not auto create instance of inner field 'inner3'.\n" +
                    "Reason: Unable to create instance of type org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$Inner3. No default (no-argument) constructor found.\n" +
                    "Reason: NoSuchMethodException: org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$Inner3.<init>()", e.getMessage());
        }
    }

    @Test
    public void fieldDoesNotExist() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(otherClassField));
        try {
            compositeFieldWrapper.setValue("value", object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for field with name 'field'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: value\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$OtherClass.field to org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$MyClass", e.getMessage());
        }
    }

    @Test
    public void compositeFieldDoesNotExist() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, otherClassField));
        try {
            compositeFieldWrapper.setValue("value", object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for field with name 'field'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: value\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$OtherClass.field to org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$Inner1", e.getMessage());
        }
    }

    @Test
    public void innerFieldDoesNotExist() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, otherClassField, inner2Field));
        try {
            compositeFieldWrapper.setValue("value", object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for composite field with name 'inner1.field.field'. Cannot get value of inner field 'field'.\n" +
                    "Reason: Unable to get value of field with name 'field'.\n" +
                    "Make sure that the field exists on the target object.\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$OtherClass.field to org.unitilsnew.core.reflect.CompositeFieldWrapperSetValueTest$Inner1", e.getMessage());
        }
    }

    @Test
    public void nullObject() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));
        try {
            compositeFieldWrapper.setValue("value", null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to set value for composite field with name 'inner1.field'. Object cannot be null.", e.getMessage());
        }
    }


    public static class MyClass {

        private String field;
        private Inner1 inner1;

    }

    public static class Inner1 {

        private String field;
        private Inner2 inner2;
    }

    public static class Inner2 {

        private String field;
        private Inner3 inner3;

    }

    public static class Inner3 {

        private String field;

        public Inner3(String someArg) {
        }
    }

    public static class OtherClass {

        private String field;

    }
}
