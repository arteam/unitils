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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperGetCompositeFieldTest {

    /* Tested object */
    private ClassWrapper classWrapper;

    private Field superclassField;
    private Field myClassField;
    private Field myClassInner1Field;
    private Field inner1Field;
    private Field inner1Inner2Field;
    private Field inner2Field;


    @Before
    public void initialize() throws Exception {
        classWrapper = new ClassWrapper(MyClass.class);

        superclassField = SuperClass.class.getDeclaredField("superField");
        myClassField = MyClass.class.getDeclaredField("field");
        myClassInner1Field = MyClass.class.getDeclaredField("inner1");
        inner1Field = Inner1.class.getDeclaredField("field");
        inner1Inner2Field = Inner1.class.getDeclaredField("inner2");
        inner2Field = Inner2.class.getDeclaredField("field");
    }


    @Test
    public void simpleField() throws Exception {
        CompositeFieldWrapper result = classWrapper.getCompositeField("field");
        assertEquals(asList(myClassField), result.getWrappedFields());
    }


    @Test
    public void simpleFieldOnSuperClass() throws Exception {
        CompositeFieldWrapper result = classWrapper.getCompositeField("superField");
        assertEquals(asList(superclassField), result.getWrappedFields());
    }

    @Test
    public void simpleFieldDoesNotExist() throws Exception {
        try {
            classWrapper.getCompositeField("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field for property 'xxx'. Field with name 'xxx' does not exist on class org.unitilsnew.core.reflect.ClassWrapperGetCompositeFieldTest$MyClass or one of its superclasses.", e.getMessage());
        }
    }

    @Test
    public void oneLevelCompositeField() throws Exception {
        CompositeFieldWrapper result = classWrapper.getCompositeField("inner1.field");
        assertEquals(asList(myClassInner1Field, inner1Field), result.getWrappedFields());
    }

    @Test
    public void twoLevelCompositeField() throws Exception {
        CompositeFieldWrapper result = classWrapper.getCompositeField("inner1.inner2.field");
        assertEquals(asList(myClassInner1Field, inner1Inner2Field, inner2Field), result.getWrappedFields());
    }

    @Test
    public void compositeFieldOnSuperClass() throws Exception {
        CompositeFieldWrapper result = classWrapper.getCompositeField("inner1.inner2.superField");
        assertEquals(asList(myClassInner1Field, inner1Inner2Field, superclassField), result.getWrappedFields());
    }

    @Test
    public void fieldNamesAreTrimmed() throws Exception {
        CompositeFieldWrapper result = classWrapper.getCompositeField(" inner1 . inner2 .  superField  ");
        assertEquals(asList(myClassInner1Field, inner1Inner2Field, superclassField), result.getWrappedFields());
    }

    @Test
    public void invalidStartingDot() throws Exception {
        try {
            classWrapper.getCompositeField(".field");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid property expression '.field'. Make sure the expression follows following pattern: field1(.field2.(field3)).", e.getMessage());
        }
    }

    @Test
    public void invalidEndingDot() throws Exception {
        try {
            classWrapper.getCompositeField("field.");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid property expression 'field.'. Make sure the expression follows following pattern: field1(.field2.(field3)).", e.getMessage());
        }
    }

    @Test
    public void invalidMiddleDot() throws Exception {
        try {
            classWrapper.getCompositeField("inner1..field");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Invalid property expression 'inner1..field'. Make sure the expression follows following pattern: field1(.field2.(field3)).", e.getMessage());
        }
    }

    @Test
    public void nullName() throws Exception {
        try {
            classWrapper.getCompositeField(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field. Property cannot be null or empty.", e.getMessage());
        }
    }

    @Test
    public void emptyName() throws Exception {
        try {
            classWrapper.getCompositeField("  ");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get field. Property cannot be null or empty.", e.getMessage());
        }
    }


    public static class SuperClass {

        private String field;
        private String superField;
    }

    public static class MyClass extends SuperClass {

        private String field;
        private Inner1 inner1;
    }

    public static class Inner1 {

        private String field;
        private Inner2 inner2;
    }

    public static class Inner2 extends SuperClass {

        private String field;
    }
}
