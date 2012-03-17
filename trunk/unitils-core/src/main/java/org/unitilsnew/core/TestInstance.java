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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.addAll;

/**
 * @author Tim Ducheyne
 */
public class TestInstance {

    protected TestClass testClass;
    protected Method testMethod;
    protected Object testObject;

    protected List<TestField> testFields;
    protected List<Annotation> methodAnnotations;


    public TestInstance(TestClass testClass, Object testObject, Method testMethod) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.testObject = testObject;
    }


    public TestClass getTestClass() {
        return testClass;
    }

    public Method getTestMethod() {
        return testMethod;
    }

    public Object getTestObject() {
        return testObject;
    }

    public String getName() {
        return testClass.getName() + "." + testMethod.getName();
    }


    public List<TestField> getTestFields() {
        if (testFields != null) {
            return testFields;
        }
        testFields = new ArrayList<TestField>();
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
        return testClass.getAnnotations(annotationClass);
    }

    public List<Annotation> getClassAnnotations() {
        return testClass.getAnnotations();
    }

    public <A extends Annotation> boolean hasClassAnnotation(Class<A> annotationClass) {
        return testClass.hasAnnotation(annotationClass);
    }


    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return testMethod.getAnnotation(annotationClass);
    }

    public List<Annotation> getMethodAnnotations() {
        if (methodAnnotations != null) {
            return methodAnnotations;
        }
        methodAnnotations = new ArrayList<Annotation>();
        addAll(methodAnnotations, testMethod.getDeclaredAnnotations());
        return methodAnnotations;
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationClass) {
        return testMethod.isAnnotationPresent(annotationClass);
    }


    protected void addTestFields(Object testObject, List<TestField> testFields) {
        List<Field> fields = testClass.getFields();
        for (Field field : fields) {
            TestField testField = new TestField(field, testObject);
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
        if (testClass != null ? !testClass.equals(that.testClass) : that.testClass != null) {
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
        int result = testClass != null ? testClass.hashCode() : 0;
        result = 31 * result + (testMethod != null ? testMethod.hashCode() : 0);
        result = 31 * result + (testObject != null ? testObject.hashCode() : 0);
        return result;
    }
}
