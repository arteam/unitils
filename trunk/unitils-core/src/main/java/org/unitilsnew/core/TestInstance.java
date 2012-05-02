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

import org.unitilsnew.core.reflect.ClassWrapper;
import org.unitilsnew.core.reflect.FieldWrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

/**
 * @author Tim Ducheyne
 */
public class TestInstance {

    protected ClassWrapper classWrapper;
    protected Method testMethod;
    protected Object testObject;


    public TestInstance(ClassWrapper classWrapper, Object testObject, Method testMethod) {
        this.classWrapper = classWrapper;
        this.testMethod = testMethod;
        this.testObject = testObject;
    }


    public ClassWrapper getClassWrapper() {
        return classWrapper;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public Object getTestObject() {
        return testObject;
    }

    public String getName() {
        return classWrapper.getName() + "." + testMethod.getName();
    }


    public TestField getTestField(String name) {
        FieldWrapper fieldWrapper = classWrapper.getField(name);
        return new TestField(fieldWrapper, testObject);
    }

    public List<TestField> getTestFields(List<String> names) {
        List<FieldWrapper> fieldWrappers = classWrapper.getFields(names);
        List<TestField> testFields = new ArrayList<TestField>(fieldWrappers.size());
        for (FieldWrapper fieldWrapper : fieldWrappers) {
            TestField testField = new TestField(fieldWrapper, testObject);
            testFields.add(testField);
        }
        return testFields;
    }

    public List<TestField> getTestFields() {
        List<TestField> testFields = new ArrayList<TestField>();
        addTestFields(testObject, testFields);
        return testFields;
    }

    public List<TestField> getTestFieldsOfType(Class<?> type) {
        List<TestField> testFields = getTestFields();
        List<TestField> result = new ArrayList<TestField>(testFields.size());

        for (TestField testField : testFields) {
            if (testField.isOfType(type)) {
                result.add(testField);
            }
        }
        return result;
    }

    public <A extends Annotation> List<TestField> getTestFieldsWithAnnotation(Class<A> annotationClass) {
        List<TestField> testFields = getTestFields();
        List<TestField> result = new ArrayList<TestField>(testFields.size());

        for (TestField testField : testFields) {
            if (testField.hasAnnotation(annotationClass)) {
                result.add(testField);
            }
        }
        return result;
    }

    public <A extends Annotation> List<A> getClassAnnotations(Class<A> annotationClass) {
        return classWrapper.getAnnotations(annotationClass);
    }

    public List<Annotation> getClassAnnotations() {
        return classWrapper.getAnnotations();
    }

    public <A extends Annotation> boolean hasClassAnnotation(Class<A> annotationClass) {
        return classWrapper.hasAnnotation(annotationClass);
    }


    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return testMethod.getAnnotation(annotationClass);
    }

    public List<Annotation> getMethodAnnotations() {
        List<Annotation> methodAnnotations = new ArrayList<Annotation>();
        addAll(methodAnnotations, testMethod.getDeclaredAnnotations());
        return methodAnnotations;
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationClass) {
        return testMethod.isAnnotationPresent(annotationClass);
    }


    protected void addTestFields(Object testObject, List<TestField> testFields) {
        List<FieldWrapper> fieldWrappers = classWrapper.getFields();
        for (FieldWrapper fieldWrapper : fieldWrappers) {
            TestField testField = new TestField(fieldWrapper, testObject);
            testFields.add(testField);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestInstance that = (TestInstance) o;
        if (classWrapper != null ? !classWrapper.equals(that.classWrapper) : that.classWrapper != null) {
            return false;
        }
        if (testMethod != null ? !testMethod.equals(that.testMethod) : that.testMethod != null) {
            return false;
        }
        if (testObject != null ? !testObject.equals(that.testObject) : that.testObject != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = classWrapper != null ? classWrapper.hashCode() : 0;
        result = 31 * result + (testMethod != null ? testMethod.hashCode() : 0);
        result = 31 * result + (testObject != null ? testObject.hashCode() : 0);
        return result;
    }
}
