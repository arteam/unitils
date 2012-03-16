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

import org.unitils.core.UnitilsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;
import static org.unitils.util.ReflectionUtils.getGenericParameterClass;

/**
 * @author Tim Ducheyne
 */
public class TestField {

    protected Field field;
    protected Object testObject;

    protected List<Annotation> fieldAnnotations;


    public TestField(Field field, Object testObject) {
        this.field = field;
        this.testObject = testObject;
    }


    public Field getField() {
        return field;
    }

    public String getName() {
        return field.getName();
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Class<?> getGenericType() {
        return getGenericParameterClass(field.getGenericType());
    }

    public boolean isOfType(Class<?> type) {
        Class<?> fieldType = field.getType();
        return type.isAssignableFrom(fieldType);
    }


    @SuppressWarnings("unchecked")
    public <T> T getValue(Object value) {
        try {
            field.setAccessible(true);
            return (T) field.get(testObject);

        } catch (Exception e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }

    public void setValue(Object value) {
        try {
            field.setAccessible(true);
            field.set(testObject, value);

        } catch (Exception e) {
            throw new UnitilsException("Unable to set value of field " + field.getName()
                    + ". Ensure that the value is of the correct type. Field type: " + field.getType() + ". Value: " + value, e);
        }
    }


    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return field.getAnnotation(annotationClass);
    }

    public List<Annotation> getAnnotations() {
        if (fieldAnnotations != null) {
            return fieldAnnotations;
        }
        fieldAnnotations = new ArrayList<Annotation>();
        addAll(fieldAnnotations, field.getDeclaredAnnotations());
        return fieldAnnotations;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass) {
        return field.getAnnotation(annotationClass) != null;
    }


    @Override
    public String toString() {
        return getName();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TestField testField = (TestField) o;
        if (field != null ? !field.equals(testField.field) : testField.field != null) {
            return false;
        }
        if (testObject != null ? !testObject.equals(testField.testObject) : testField.testObject != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (testObject != null ? testObject.hashCode() : 0);
        return result;
    }
}
