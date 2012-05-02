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

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperIsAssignableFromTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field myTypeField;
    private Field myTypeListField;
    private Field subTypeListField;
    private Field wildCardMyTypeListField;
    private Field wildCardField;


    @Before
    public void initialize() throws Exception {
        myTypeField = MyClass.class.getDeclaredField("myTypeField");
        myTypeListField = MyClass.class.getDeclaredField("myTypeListField");
        subTypeListField = MyClass.class.getDeclaredField("subTypeListField");
        wildCardMyTypeListField = MyClass.class.getDeclaredField("wildCardMyTypeListField");
        wildCardField = MyClass.class.getDeclaredField("wildCardField");
    }


    @Test
    public void assignableClassType() {
        fieldWrapper = new FieldWrapper(myTypeField);

        boolean result = fieldWrapper.isAssignableFrom(SubType.class);
        assertTrue(result);
    }

    @Test
    public void notAssignableClassType() {
        fieldWrapper = new FieldWrapper(myTypeField);

        boolean result = fieldWrapper.isAssignableFrom(OtherType.class);
        assertFalse(result);
    }

    @Test
    public void equalClassType() {
        fieldWrapper = new FieldWrapper(myTypeField);

        boolean result = fieldWrapper.isAssignableFrom(MyType.class);
        assertTrue(result);
    }

    @Test
    public void assignableGenericType() throws Exception {
        Type genericType = subTypeListField.getGenericType();
        fieldWrapper = new FieldWrapper(wildCardMyTypeListField);

        boolean result = fieldWrapper.isAssignableFrom(genericType);
        assertTrue(result);
    }

    @Test
    public void equalGenericType() throws Exception {
        Type genericType = myTypeField.getGenericType();
        fieldWrapper = new FieldWrapper(myTypeField);

        boolean result = fieldWrapper.isAssignableFrom(genericType);
        assertTrue(result);
    }

    @Test
    public void subTypeIsNotAssignableWhenThereIsNoWildCard() throws Exception {
        Type genericType = subTypeListField.getGenericType();
        fieldWrapper = new FieldWrapper(myTypeListField);

        boolean result = fieldWrapper.isAssignableFrom(genericType);
        assertFalse(result);
    }

    @Test
    public void assignableToWildCardType() throws Exception {
        Type genericType = myTypeListField.getGenericType();
        fieldWrapper = new FieldWrapper(wildCardField);

        boolean result = fieldWrapper.isAssignableFrom(genericType);
        assertTrue(result);
    }


    public static class MyClass {

        private MyType myTypeField;

        private List<MyType> myTypeListField;
        private List<SubType> subTypeListField;
        private List<? extends MyType> wildCardMyTypeListField;
        private List<?> wildCardField;
    }

    private static class MyType {
    }

    private static class SubType extends MyType {
    }

    private static class OtherType {
    }
}
