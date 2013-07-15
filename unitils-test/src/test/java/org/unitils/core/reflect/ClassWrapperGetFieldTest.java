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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperGetFieldTest {

    /* Tested object */
    private ClassWrapper classWrapper;

    private Field superclassField;
    private Field myClassField;


    @Before
    public void initialize() throws Exception {
        classWrapper = new ClassWrapper(MyClass.class);

        superclassField = SuperClass.class.getDeclaredField("superField");
        myClassField = MyClass.class.getDeclaredField("field");
    }


    @Test
    public void field() throws Exception {
        FieldWrapper result = classWrapper.getField("field");
        assertEquals(myClassField, result.getWrappedField());
    }

    @Test
    public void fieldOnSuperClass() throws Exception {
        FieldWrapper result = classWrapper.getField("superField");
        assertEquals(superclassField, result.getWrappedField());
    }

    @Test
    public void fieldDoesNotExist() throws Exception {
        try {
            classWrapper.getField("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field with name 'xxx'. No such field exists on class org.unitils.core.reflect.ClassWrapperGetFieldTest$MyClass or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void fieldNameAreTrimmed() throws Exception {
        FieldWrapper result = classWrapper.getField(" field  ");
        assertEquals(myClassField, result.getWrappedField());
    }

    @Test
    public void nullName() throws Exception {
        try {
            classWrapper.getField(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field. Name cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void emptyName() throws Exception {
        try {
            classWrapper.getField("  ");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field. Name cannot be null or empty.", e.getMessage());
        }
    }


    public static class SuperClass {

        private String field;
        private String superField;
    }

    public static class MyClass extends SuperClass {

        private String field;
    }
}
