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

package org.unitils.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.reflect.FieldWrapper;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestFieldHashCodeTest {

    private FieldWrapper fieldWrapper;


    @Before
    public void initialize() throws Exception {
        Field field = MyClass.class.getDeclaredField("field");
        fieldWrapper = new FieldWrapper(field);
    }


    @Test
    public void hashCodeForClass() {
        TestField testField = new TestField(fieldWrapper, "a");
        int result = testField.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        TestField testField1 = new TestField(fieldWrapper, "a");
        TestField testField2 = new TestField(fieldWrapper, "a");

        assertEquals(testField1.hashCode(), testField2.hashCode());
    }

    @Test
    public void nullField() {
        TestField testField = new TestField(null, "a");
        int result = testField.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullTestObject() {
        TestField testField = new TestField(fieldWrapper, null);
        int result = testField.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullFieldAndTestObject() {
        TestField testField = new TestField(null, null);
        int result = testField.hashCode();

        assertEquals(0, result);
    }


    private static class MyClass {

        private String field;
    }
}
