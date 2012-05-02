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
import org.unitilsnew.UnitilsJUnit4;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class OriginalFieldValueRestoreToOriginalValueTest extends UnitilsJUnit4 {

    /* Tested object */
    private OriginalFieldValue originalFieldValue;

    private MyClass object;
    private FieldWrapper fieldWrapper;


    @Before
    public void initialize() throws Exception {
        Field field = MyClass.class.getDeclaredField("value");
        fieldWrapper = new FieldWrapper(field);
        object = new MyClass("value");
    }


    @Test
    public void originalValue() {
        originalFieldValue = new OriginalFieldValue("original value", fieldWrapper, object);

        originalFieldValue.restoreToOriginalValue();
        assertEquals("original value", object.value);
    }

    @Test
    public void nullValue() {
        originalFieldValue = new OriginalFieldValue(null, fieldWrapper, object);

        originalFieldValue.restoreToOriginalValue();
        assertNull(object.value);
    }

    @Test
    public void exceptionWhenUnableToRestore() {
        try {
            originalFieldValue = new OriginalFieldValue("original value", fieldWrapper, "xxx");
            originalFieldValue.restoreToOriginalValue();
        } catch (UnitilsException e) {
            assertEquals("Unable to restore field with name 'value' to value 'original value'. Reason:\n" +
                    "Unable to set value for field with name 'value'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: original value", e.getMessage());
        }
    }


    private static class MyClass {

        private String value;

        private MyClass(String value) {
            this.value = value;
        }
    }
}
