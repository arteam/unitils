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

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TestFieldSetValueTest {

    /* Tested object */
    private TestField testField;

    private Field field;
    private MyClass testObject;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        testObject = new MyClass();

        testField = new TestField(field, testObject);
    }


    @Test
    public void getField() {
        Field result = testField.getField();
        assertSame(field, result);
    }

    @Test
    public void getValue() {
        testObject.field = "value";

        String result = testField.getValue("value");
        assertEquals("value", result);
    }

    @Test
    public void setValue() {
        testField = new TestField(field, testObject);

        testField.setValue("value");
        assertEquals("value", testObject.field);
    }


    private static class MyClass {

        private String field;
    }
}
