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
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class TestFieldGetGenericTypeTest {

    /* Tested object */
    private TestField testField;

    private Field field;
    private Field genericField;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        genericField = MyClass.class.getDeclaredField("genericField");
    }


    @Test
    public void classType() {
        testField = new TestField(new FieldWrapper(field), null);

        Type result = testField.getGenericType();
        assertEquals(String.class, result);
    }

    @Test
    public void genericType() {
        testField = new TestField(new FieldWrapper(genericField), null);

        Type result = testField.getGenericType();
        assertEquals(genericField.getGenericType(), result);
    }


    private static class MyClass {

        private String field;
        private List<String> genericField;
    }
}
