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
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertPropertyLenientEquals;

/**
 * @author Tim Ducheyne
 */
public class ClassWrapperGetFieldsAssignableFromTest {

    /* Tested object */
    private ClassWrapper classWrapper;

    private Field field1a;
    private Field field1b;
    private Field field2;
    private Field field4;
    private Field field5;


    @Before
    public void initialize() throws Exception {
        field1a = SuperClass.class.getDeclaredField("field1");
        field2 = SuperClass.class.getDeclaredField("field2");
        field1b = MyClass.class.getDeclaredField("field1");
        field4 = MyClass.class.getDeclaredField("field4");
        field5 = MyClass.class.getDeclaredField("field5");
    }


    @Test
    public void getFieldsAssignableFrom() {
        classWrapper = new ClassWrapper(MyClass.class);

        List<FieldWrapper> result = classWrapper.getFieldsAssignableFrom(Type1.class);
        assertPropertyLenientEquals("wrappedField", asList(field1a, field1b, field2, field4, field5), result);
    }

    @Test
    public void emptyWhenNoFieldsFoundOfType() {
        classWrapper = new ClassWrapper(MyClass.class);

        List<FieldWrapper> result = classWrapper.getFieldsAssignableFrom(Map.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenNoFields() {
        classWrapper = new ClassWrapper(NoFieldsClass.class);

        List<FieldWrapper> result = classWrapper.getFieldsAssignableFrom(Type1.class);
        assertTrue(result.isEmpty());
    }

    @Test
    public void exceptionWhenNullType() {
        try {
            classWrapper = new ClassWrapper(MyClass.class);
            classWrapper.getFieldsAssignableFrom(null);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get fields assignable from type. Type cannot be null.", e.getMessage());
        }
    }


    private static class SuperClass {

        private Type1 field1;
        private SuperType field2;
        private OtherType field3;
    }

    private static class MyClass extends SuperClass {

        private static Type1 staticFieldIsIgnored;

        private Type1 field1;
        private Type1 field4;
        private SuperType field5;
        private OtherType field6;
    }

    private static class NoFieldsClass {
    }


    private static class SuperType {
    }

    private static class Type1 extends SuperType {
    }

    private static class OtherType {
    }
}
