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

package org.unitilsnew.core;

import org.junit.Before;
import org.junit.Test;
import org.unitilsnew.core.reflect.FieldWrapper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestFieldEqualsTest {

    private FieldWrapper field1;
    private FieldWrapper field2;


    @Before
    public void initialize() throws Exception {
        field1 = new FieldWrapper(MyClass.class.getDeclaredField("field1"));
        field2 = new FieldWrapper(MyClass.class.getDeclaredField("field2"));
    }


    @Test
    public void equal() {
        TestField testField1 = new TestField(field1, "a");
        TestField testField2 = new TestField(field1, "a");

        assertTrue(testField1.equals(testField2));
        assertTrue(testField2.equals(testField1));
    }

    @Test
    public void same() {
        TestField testField = new TestField(field1, "a");

        assertTrue(testField.equals(testField));
    }

    @Test
    public void notEqualField() {
        TestField testField1 = new TestField(field1, "a");
        TestField testField2 = new TestField(field2, "a");

        assertFalse(testField1.equals(testField2));
        assertFalse(testField2.equals(testField1));
    }

    @Test
    public void notEqualTestObject() {
        TestField testField1 = new TestField(field1, "a");
        TestField testField2 = new TestField(field1, "b");

        assertFalse(testField1.equals(testField2));
        assertFalse(testField2.equals(testField1));
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void notEqualToNull() {
        TestField testField = new TestField(field1, "a");

        assertFalse(testField.equals(null));
    }

    @Test
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public void notEqualToOtherType() {
        TestField testField = new TestField(field1, "a");

        assertFalse(testField.equals("xxx"));
    }

    @Test
    public void nullFields() {
        TestField testField1 = new TestField(null, "a");
        TestField testField2 = new TestField(null, "a");

        assertTrue(testField1.equals(testField2));
    }

    @Test
    public void nullTestObjects() {
        TestField testField1 = new TestField(field1, null);
        TestField testField2 = new TestField(field1, null);

        assertTrue(testField1.equals(testField2));
    }

    @Test
    public void nullFieldsAndTestObjects() {
        TestField testField1 = new TestField(null, null);
        TestField testField2 = new TestField(null, null);

        assertTrue(testField1.equals(testField2));
    }


    private static class MyClass {

        private String field1;
        private String field2;
    }
}
