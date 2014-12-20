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
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperEqualsTest {

    private Field stringField;
    private Field listField;


    @Before
    public void initialize() throws Exception {
        stringField = MyClass.class.getDeclaredField("stringField");
        listField = MyClass.class.getDeclaredField("listField");
    }


    @Test
    public void equal() {
        FieldWrapper fieldWrapper1 = new FieldWrapper(stringField);
        FieldWrapper fieldWrapper2 = new FieldWrapper(stringField);

        assertTrue(fieldWrapper1.equals(fieldWrapper2));
        assertTrue(fieldWrapper2.equals(fieldWrapper1));
    }

    @Test
    public void same() {
        FieldWrapper fieldWrapper = new FieldWrapper(stringField);

        assertTrue(fieldWrapper.equals(fieldWrapper));
    }

    @Test
    public void notEqual() {
        FieldWrapper fieldWrapper1 = new FieldWrapper(stringField);
        FieldWrapper fieldWrapper2 = new FieldWrapper(listField);

        assertFalse(fieldWrapper1.equals(fieldWrapper2));
        assertFalse(fieldWrapper2.equals(fieldWrapper1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        FieldWrapper fieldWrapper = new FieldWrapper(stringField);

        assertFalse(fieldWrapper.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        FieldWrapper fieldWrapper = new FieldWrapper(stringField);

        assertFalse(fieldWrapper.equals("xxx"));
    }

    @Test
    public void nullFields() {
        FieldWrapper fieldWrapper1 = new FieldWrapper(null);
        FieldWrapper fieldWrapper2 = new FieldWrapper(null);

        assertTrue(fieldWrapper1.equals(fieldWrapper2));
    }


    private static class MyClass {

        private String stringField;
        private List listField;
    }
}
