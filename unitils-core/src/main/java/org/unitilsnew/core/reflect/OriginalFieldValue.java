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

import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;

/**
 * Class for holding values that need to be restored after a test was performed. It also contains information
 * on how and where the value needs to be restored.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class OriginalFieldValue {

    protected Object originalValue;
    protected FieldWrapper fieldWrapper;
    protected Object object;


    public OriginalFieldValue(Object originalValue, FieldWrapper fieldWrapper, Object object) {
        this.originalValue = originalValue;
        this.fieldWrapper = fieldWrapper;
        this.object = object;
    }


    public Object getOriginalValue() {
        return originalValue;
    }

    public Field getField() {
        return fieldWrapper.getWrappedField();
    }

    public FieldWrapper getFieldWrapper() {
        return fieldWrapper;
    }

    public Object getObject() {
        return object;
    }

    public void restoreToOriginalValue() {
        restoreValue(originalValue);
    }

    public void restoreToNullOr0() {
        if (fieldWrapper.getType().isPrimitive()) {
            restoreValue(0);
        } else {
            restoreValue(null);
        }
    }


    protected void restoreValue(Object value) {
        try {
            fieldWrapper.setValue(value, object);

        } catch (Exception e) {
            throw new UnitilsException("Unable to restore field with name '" + fieldWrapper.getName() + "' to value '" + value + "'.", e);
        }
    }
}