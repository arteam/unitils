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

package org.unitils.core.reflect;

import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.util.List;

import static java.lang.reflect.Modifier.isStatic;

/**
 * @author Tim Ducheyne
 */
public class CompositeFieldWrapper {

    protected List<Field> wrappedFields;


    public CompositeFieldWrapper(List<Field> wrappedFields) {
        this.wrappedFields = wrappedFields;
    }


    public List<Field> getWrappedFields() {
        return wrappedFields;
    }

    public String getName() {
        StringBuilder result = new StringBuilder();
        for (Field field : wrappedFields) {
            if (result.length() != 0) {
                result.append('.');
            }
            result.append(field.getName());
        }
        return result.toString();
    }


    @SuppressWarnings("unchecked")
    public <T> T getValue(Object object) {
        if (object == null && !isStatic(wrappedFields.get(0).getModifiers())) {
            throw new UnitilsException("Unable to get value of composite field with name '" + getName() + "'. Object cannot be null.");
        }
        Object endObject = object;
        for (int i = 0; i < wrappedFields.size() - 1; i++) {
            Field innerField = wrappedFields.get(i);
            FieldWrapper innerFieldWrapper = new FieldWrapper(innerField);

            endObject = innerFieldWrapper.getValue(endObject);
            if (endObject == null) {
                return null;
            }
        }
        Field lastField = wrappedFields.get(wrappedFields.size() - 1);
        FieldWrapper lastFieldWrapper = new FieldWrapper(lastField);

        return (T) lastFieldWrapper.getValue(endObject);
    }


    public OriginalFieldValue setValue(Object value, Object object) {
        return setValue(value, object, false);
    }

    public OriginalFieldValue setValue(Object value, Object object, boolean autoCreateInnerFields) {
        if (object == null && !isStatic(wrappedFields.get(0).getModifiers())) {
            throw new UnitilsException("Unable to set value for composite field with name '" + getName() + "'. Object cannot be null.");
        }
        OriginalFieldValue firstOriginalFieldValue = null;
        Object endObject = object;
        for (int i = 0; i < wrappedFields.size() - 1; i++) {
            Field innerField = wrappedFields.get(i);
            FieldWrapper innerFieldWrapper = new FieldWrapper(innerField);

            Object innerFieldValue;
            try {
                innerFieldValue = innerFieldWrapper.getValue(endObject);
            } catch (Exception e) {
                throw new UnitilsException("Unable to set value for composite field with name '" + getName() + "'. Cannot get value of inner field '" + innerFieldWrapper + "'.", e);
            }
            if (innerFieldValue == null) {
                if (autoCreateInnerFields) {
                    try {
                        Object autoCreatedInstance = innerFieldWrapper.getClassWrapper().createInstance();
                        OriginalFieldValue originalFieldValue = innerFieldWrapper.setValue(autoCreatedInstance, endObject);
                        if (firstOriginalFieldValue == null) {
                            firstOriginalFieldValue = originalFieldValue;
                        }
                        endObject = autoCreatedInstance;

                    } catch (Exception e) {
                        throw new UnitilsException("Unable to set value for composite field with name '" + getName() + "'. Could not auto create instance of inner field '" + innerFieldWrapper + "'.", e);
                    }
                } else {
                    throw new UnitilsException("Unable to set value for composite field with name '" + getName() + "'. Inner field with name '" + innerFieldWrapper + "' is null.");
                }
            } else {
                endObject = innerFieldValue;
            }
        }
        Field lastField = wrappedFields.get(wrappedFields.size() - 1);
        FieldWrapper lastFieldWrapper = new FieldWrapper(lastField);

        OriginalFieldValue originalFieldValue = lastFieldWrapper.setValue(value, endObject);
        if (firstOriginalFieldValue == null) {
            firstOriginalFieldValue = originalFieldValue;
        }
        return firstOriginalFieldValue;
    }


    @Override
    public String toString() {
        return getName();
    }
}
