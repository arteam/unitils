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

import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TestFieldIsAssignableFromTest {

    /* Tested object */
    private TestField testField;


    @Before
    public void initialize() throws Exception {
        Field field = MyClass.class.getDeclaredField("field");
        testField = new TestField(new FieldWrapper(field), null);
    }


    @Test
    public void equalType() {
        boolean result = testField.isAssignableFrom(Type1.class);
        assertTrue(result);
    }

    @Test
    public void assignableType() {
        boolean result = testField.isAssignableFrom(Type2.class);
        assertTrue(result);
    }

    @Test
    public void falseWhenNotAssignableType() {
        boolean result = testField.isAssignableFrom(Type3.class);
        assertFalse(result);
    }


    private static class MyClass {

        private Type1 field;
    }


    private static class Type1 {
    }

    private static class Type2 extends Type1 {
    }

    private static class Type3 {
    }
}
