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
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class TestFieldGetGenericTypeTest {

    /* Tested object */
    private TestField testField;

    private Field regularField;
    private Field genericField;
    private Field multiGenericField;
    private MyClass testObject;


    @Before
    public void initialize() throws Exception {
        regularField = MyClass.class.getDeclaredField("regularField");
        genericField = MyClass.class.getDeclaredField("genericField");
        multiGenericField = MyClass.class.getDeclaredField("multiGenericField");
        testObject = new MyClass();
    }


    @Test
    public void genericField() {
        testField = new TestField(genericField, testObject);

        Class<?> result = testField.getGenericType();
        assertEquals(String.class, result);
    }

    @Test
    public void nullWhenNoGenericType() {
        testField = new TestField(regularField, testObject);

        Class<?> result = testField.getGenericType();
        assertNull(result);
    }

    @Test(expected = UnitilsException.class)
    public void exceptionWhenMoreThanOneGenericType() {
        testField = new TestField(multiGenericField, testObject);
        testField.getGenericType();
    }


    private static class MyClass {

        private String regularField;
        private List<String> genericField;
        private Map<String, String> multiGenericField;
    }
}
