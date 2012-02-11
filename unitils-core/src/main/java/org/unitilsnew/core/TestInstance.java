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
import org.unitilsnew.core.config.Configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author Tim Ducheyne
 */
public class TestInstance {

    protected TestClass testClass;
    protected Method testMethod;
    protected Object testObject;

    protected Configuration configuration;


    public TestInstance(TestClass testClass, Method testMethod, Object testObject, Configuration configuration) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.testObject = testObject;
        this.configuration = configuration;
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

    public <A extends Annotation> TestAnnotation<A> getTestAnnotation(Class<A> annotationClass) {
        List<A> classAnnotations = testClass.getClassAnnotations(annotationClass);
        A methodAnnotation = getMethodAnnotation(annotationClass);
        return new TestAnnotation<A>(methodAnnotation, classAnnotations, configuration);
    }

    public <A extends Annotation> List<FieldAnnotation<A>> getFieldAnnotations(Class<A> annotationClass) {
        return testClass.getFieldAnnotations(annotationClass);
    }

    public <A extends Annotation> A getMethodAnnotation(Class<A> annotationClass) {
        return testMethod.getAnnotation(annotationClass);
    }

    public <A extends Annotation> boolean hasMethodAnnotation(Class<A> annotationClass) {
        return testMethod.getAnnotation(annotationClass) != null;
    }

    public void setFieldValue(Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(testObject, value);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Unable to set value of field " + field.getName()
                    + ". Ensure that the field is of the correct type. Value: " + value, e);

        } catch (IllegalAccessException e) {
            // cannot occur since field.accessible has been set to true
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }
}
