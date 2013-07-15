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

import org.unitils.core.reflect.ClassWrapper;
import org.unitils.core.reflect.FieldWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class TestField {

    protected FieldWrapper fieldWrapper;
    protected Object testObject;

    protected List<Annotation> fieldAnnotations;


    public TestField(FieldWrapper fieldWrapper, Object testObject) {
        this.fieldWrapper = fieldWrapper;
        this.testObject = testObject;
    }


    public Field getField() {
        return fieldWrapper.getWrappedField();
    }

    public Object getTestObject() {
        return testObject;
    }

    public String getName() {
        return fieldWrapper.getName();
    }

    public Class<?> getType() {
        return fieldWrapper.getType();
    }

    public Type getGenericType() {
        return fieldWrapper.getGenericType();
    }

    public ClassWrapper getClassWrapper() {
        return fieldWrapper.getClassWrapper();
    }


    public boolean isOfType(Type type) {
        return fieldWrapper.isOfType(type);
    }

    public boolean isAssignableFrom(Class<?> type) {
        return fieldWrapper.isAssignableFrom(type);
    }


    public Class<?> getSingleGenericClass() {
        return fieldWrapper.getSingleGenericClass();
    }

    public Type getSingleGenericType() {
        return fieldWrapper.getSingleGenericType();
    }


    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) fieldWrapper.getValue(testObject);
    }

    public void setValue(Object value) {
        fieldWrapper.setValue(value, testObject);
    }


    public <A extends Annotation> A getAnnotation(Class<A> annotationClass) {
        return fieldWrapper.getAnnotation(annotationClass);
    }

    public List<Annotation> getAnnotations() {
        return fieldWrapper.getAnnotations();
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass) {
        return fieldWrapper.hasAnnotation(annotationClass);
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
        if (fieldWrapper != null ? !fieldWrapper.equals(testField.fieldWrapper) : testField.fieldWrapper != null) {
            return false;
        }
        if (testObject != null ? !testObject.equals(testField.testObject) : testField.testObject != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldWrapper != null ? fieldWrapper.hashCode() : 0;
        result = 31 * result + (testObject != null ? testObject.hashCode() : 0);
        return result;
    }
}
