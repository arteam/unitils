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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapper {

    protected Field wrappedField;


    public FieldWrapper(Field wrappedField) {
        this.wrappedField = wrappedField;
    }


    public Field getWrappedField() {
        return wrappedField;
    }

    public String getName() {
        return wrappedField.getName();
    }

    public Class<?> getType() {
        return wrappedField.getType();
    }

    public Type getGenericType() {
        return wrappedField.getGenericType();
    }

    public TypeWrapper getTypeWrapper() {
        Type genericType = getGenericType();
        return new TypeWrapper(genericType);
    }

    public ClassWrapper getClassWrapper() {
        Class<?> type = getType();
        return new ClassWrapper(type);
    }

    public boolean isOfType(Type type) {
        if (type instanceof Class) {
            return getClassWrapper().isOfType(type);
        }
        return getTypeWrapper().isOfType(type);
    }

    public boolean isAssignableFrom(Type type) {
        return getTypeWrapper().isAssignableFrom(type);
    }

    public boolean isStatic() {
        return Modifier.isStatic(wrappedField.getModifiers());
    }


    public Class<?> getSingleGenericClass() {
        return getTypeWrapper().getSingleGenericClass();
    }

    public Type getSingleGenericType() {
        return getTypeWrapper().getSingleGenericType();
    }


    /**
     * Returns the value of the given field (may be private) in the given object
     *
     * @param object The object containing the field, null for static fields
     * @return The value of the given field in the given object
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(Object object) {
        if (object == null && !isStatic()) {
            throw new UnitilsException("Unable to get value of field with name '" + getName() + "'. Object cannot be null.");
        }
        try {
            wrappedField.setAccessible(true);
            return (T) wrappedField.get(object);

        } catch (Exception e) {
            throw new UnitilsException("Unable to get value of field with name '" + getName() + "'.\n" +
                    "Make sure that the field exists on the target object.", e);
        }
    }

    /**
     * Sets the given value to the given field (may be private) on the given object
     *
     * @param value  The value for the given field in the given object
     * @param object The object containing the field, not null
     */
    public OriginalFieldValue setValue(Object value, Object object) {
        if (object == null && !isStatic()) {
            throw new UnitilsException("Unable to set value for field with name '" + getName() + "'. Object cannot be null.");
        }
        try {
            wrappedField.setAccessible(true);
            Object originalValue = wrappedField.get(object);
            wrappedField.set(object, value);
            return new OriginalFieldValue(originalValue, this, object);

        } catch (Exception e) {
            throw new UnitilsException("Unable to set value for field with name '" + getName() + "'.\n" +
                    "Make sure that the field exists on the target object and that the value is of the correct type: " + getType().getName() + ". Value: " + value, e);
        }
    }


    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return wrappedField.getAnnotation(annotationClass);
    }

    public List<Annotation> getAnnotations() {
        List<Annotation> fieldAnnotations = new ArrayList<Annotation>();
        addAll(fieldAnnotations, wrappedField.getDeclaredAnnotations());
        return fieldAnnotations;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass) {
        return wrappedField.getAnnotation(annotationClass) != null;
    }


    @Override
    public boolean equals(Object value) {
        if (this == value) {
            return true;
        }
        if (value == null || getClass() != value.getClass()) {
            return false;
        }
        FieldWrapper that = (FieldWrapper) value;
        if (wrappedField != null ? !wrappedField.equals(that.wrappedField) : that.wrappedField != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return wrappedField != null ? wrappedField.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getName();
    }
}
