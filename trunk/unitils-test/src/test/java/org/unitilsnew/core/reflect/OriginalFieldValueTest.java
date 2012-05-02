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
import org.unitilsnew.UnitilsJUnit4;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class OriginalFieldValueTest extends UnitilsJUnit4 {

    /* Tested object */
    private OriginalFieldValue originalFieldValue;

    private Properties value;
    private Field field;
    private MyClass object;
    private FieldWrapper fieldWrapper;


    @Before
    public void initialize() throws Exception {
        value = new Properties();
        field = MyClass.class.getDeclaredField("value");
        object = new MyClass();
        fieldWrapper = new FieldWrapper(field);

        originalFieldValue = new OriginalFieldValue(value, fieldWrapper, object);
    }


    @Test
    public void getOriginalValue() {
        Object result = originalFieldValue.getOriginalValue();
        assertSame(value, result);
    }

    @Test
    public void getField() {
        Field result = originalFieldValue.getField();
        assertSame(field, result);
    }

    @Test
    public void getFieldWrapper() {
        FieldWrapper result = originalFieldValue.getFieldWrapper();
        assertSame(fieldWrapper, result);
    }

    @Test
    public void getObject() {
        Object result = originalFieldValue.getObject();
        assertSame(object, result);
    }


    private static class MyClass {

        private Properties value;
    }
}
