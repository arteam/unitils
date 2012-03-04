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
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Tim Ducheyne
 */
public class TestInstance {

    protected TestClass testClass;
    protected Method testMethod;
    protected Object testObject;

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

    public List<Annotation> getMethodAnnotations() {
        if (methodAnnotations != null) {
            return methodAnnotations;
        }
        methodAnnotations = new ArrayList<Annotation>();
        Collections.addAll(methodAnnotations, testMethod.getDeclaredAnnotations());
        return methodAnnotations;
    }

    public Set<Class<? extends Annotation>> getAnnotationTypesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        Set<Class<? extends Annotation>> annotationTypes = new LinkedHashSet<Class<? extends Annotation>>();

        List<Class<? extends Annotation>> classAnnotationTypes = testClass.getAnnotationTypesAnnotatedWith(annotationClass);
        annotationTypes.addAll(classAnnotationTypes);

        List<Annotation> methodAnnotations = getMethodAnnotations();
        for (Annotation methodAnnotation : methodAnnotations) {
            Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
            if (annotationType.getAnnotation(annotationClass) != null) {
                annotationTypes.add(annotationType);
            }
        }
        return annotationTypes;
    }


    public <A extends Annotation> List<AnnotatedField<A>> getFieldAnnotations(Class<A> annotationClass) {
        return testClass.getAnnotatedFields(annotationClass);
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
