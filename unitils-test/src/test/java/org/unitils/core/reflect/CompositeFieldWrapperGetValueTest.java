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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class CompositeFieldWrapperGetValueTest {

    /* Tested object */
    private CompositeFieldWrapper compositeFieldWrapper;

    private Field myClassField;
    private Field myClassInner1Field;
    private Field inner1Field;
    private Field inner1Inner2Field;
    private Field inner2Field;
    private Field otherClassField;
    private MyClass object;


    @Before
    public void initialize() throws Exception {
        myClassField = MyClass.class.getDeclaredField("field");
        myClassInner1Field = MyClass.class.getDeclaredField("inner1");
        inner1Field = Inner1.class.getDeclaredField("field");
        inner1Inner2Field = Inner1.class.getDeclaredField("inner2");
        inner2Field = Inner2.class.getDeclaredField("field");
        otherClassField = OtherClass.class.getDeclaredField("field");
        object = new MyClass();
    }


    @Test
    public void simpleField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassField));

        String result = compositeFieldWrapper.getValue(object);
        assertEquals("value", result);
    }

    @Test
    public void simpleFieldNullValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassField));
        object.field = null;

        String result = compositeFieldWrapper.getValue(object);
        assertNull(result);
    }

    @Test
    public void oneLevelCompositeField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));

        String result = compositeFieldWrapper.getValue(object);
        assertEquals("inner1Value", result);
    }

    @Test
    public void twoLevelCompositeField() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Inner2Field, inner2Field));

        String result = compositeFieldWrapper.getValue(object);
        assertEquals("inner2Value", result);
    }

    @Test
    public void innerFieldNullValue() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));
        object.inner1 = null;

        String result = compositeFieldWrapper.getValue(object);
        assertNull(result);
    }

    @Test
    public void fieldDoesNotExist() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(otherClassField));
        try {
            compositeFieldWrapper.getValue(object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value of field with name 'field'.\n" +
                    "Make sure that the field exists on the target object.\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitils.core.reflect.CompositeFieldWrapperGetValueTest$OtherClass.field to org.unitils.core.reflect.CompositeFieldWrapperGetValueTest$MyClass", e.getMessage());
        }
    }

    @Test
    public void compositeFieldDoesNotExist() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, otherClassField));
        try {
            compositeFieldWrapper.getValue(object);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value of field with name 'field'.\n" +
                    "Make sure that the field exists on the target object.\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitils.core.reflect.CompositeFieldWrapperGetValueTest$OtherClass.field to org.unitils.core.reflect.CompositeFieldWrapperGetValueTest$Inner1", e.getMessage());
        }
    }

    @Test
    public void nullObject() throws Exception {
        compositeFieldWrapper = new CompositeFieldWrapper(asList(myClassInner1Field, inner1Field));
        try {
            compositeFieldWrapper.getValue(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value of composite field with name 'inner1.field'. Object cannot be null.", e.getMessage());
        }
    }


    private static class MyClass {

        private String field = "value";
        private Inner1 inner1 = new Inner1();

    }

    private static class Inner1 {

        private String field = "inner1Value";
        private Inner2 inner2 = new Inner2();
    }

    private static class Inner2 {

        private String field = "inner2Value";
    }

    private static class OtherClass {

        private String field;

    }
}
