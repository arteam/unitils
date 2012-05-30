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
public class OriginalFieldValueRestoreToNullOr0Test extends UnitilsJUnit4 {

    /* Tested object */
    private OriginalFieldValue originalFieldValue;

    private FieldWrapper objectFieldWrapper;
    private FieldWrapper primitiveFieldWrapper;
    private MyClass object;


    @Before
    public void initialize() throws Exception {
        Field objectField = MyClass.class.getDeclaredField("objectValue");
        Field primitiveField = MyClass.class.getDeclaredField("primitiveValue");
        objectFieldWrapper = new FieldWrapper(objectField);
        primitiveFieldWrapper = new FieldWrapper(primitiveField);
        object = new MyClass("value", 999);
    }


    @Test
    public void nullValueWhenObjectField() {
        originalFieldValue = new OriginalFieldValue("original value", objectFieldWrapper, object);

        originalFieldValue.restoreToNullOr0();
        assertNull(object.objectValue);
        assertEquals(999, object.primitiveValue);
    }

    @Test
    public void zeroValueWhenPrimitiveField() {
        originalFieldValue = new OriginalFieldValue("original value", primitiveFieldWrapper, object);

        originalFieldValue.restoreToNullOr0();
        assertEquals("value", object.objectValue);
        assertEquals(0, object.primitiveValue);
    }

    @Test
    public void exceptionWhenUnableToRestore() {
        try {
            originalFieldValue = new OriginalFieldValue("original value", objectFieldWrapper, "xxx");
            originalFieldValue.restoreToNullOr0();
        } catch (UnitilsException e) {
            assertEquals("Unable to restore field with name 'objectValue' to value 'null'.\n" +
                    "Reason: Unable to set value for field with name 'objectValue'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: java.lang.String. Value: null\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitilsnew.core.reflect.OriginalFieldValueRestoreToNullOr0Test$MyClass.objectValue to java.lang.String", e.getMessage());
        }
    }


    private static class MyClass {

        private String objectValue;
        private int primitiveValue;

        private MyClass(String objectValue, int primitiveValue) {
            this.objectValue = objectValue;
            this.primitiveValue = primitiveValue;
        }
    }
}
