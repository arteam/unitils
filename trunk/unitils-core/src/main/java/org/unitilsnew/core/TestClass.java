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

import static java.util.Arrays.asList;

/**
 * @author Tim Ducheyne
 */
public class TestClass {

    protected Class<?> testClass;
    protected List<Field> fields;
    protected List<Method> methods;

    protected List<Annotation> annotations;


    public TestClass(Class<?> testClass) {
        this.testClass = testClass;
    }


    public String getName() {
        return testClass.getSimpleName();
    }

    public Class<?> getTestClass() {
        return testClass;
    }

    public List<Field> getFields() {
        if (fields != null) {
            return fields;
        }
        fields = new ArrayList<Field>();
        addFields(testClass, fields);
        return fields;
    }

    public List<Method> getMethods() {
        if (methods != null) {
            return methods;
        }
        methods = new ArrayList<Method>();
        addMethods(testClass, methods);
        return methods;
    }


    @SuppressWarnings("unchecked")
    public <A extends Annotation> List<A> getAnnotations(Class<A> annotationClass) {
        List<A> result = new ArrayList<A>(3);

        List<Annotation> annotations = getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(annotationClass)) {
                result.add((A) annotation);
            }
        }
        return result;
    }

    public List<Annotation> getAnnotations() {
        if (annotations != null) {
            return annotations;
        }
        annotations = new ArrayList<Annotation>(3);
        addAnnotations(testClass, annotations);
        return annotations;
    }

    public <A extends Annotation> boolean hasAnnotation(Class<A> annotationClass) {
        return testClass.isAnnotationPresent(annotationClass);
    }


    protected void addFields(Class<?> clazz, List<Field> fields) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Field[] classFields = clazz.getDeclaredFields();
        fields.addAll(asList(classFields));
        addFields(clazz.getSuperclass(), fields);
    }

    protected void addMethods(Class<?> clazz, List<Method> methods) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Method[] classMethods = clazz.getDeclaredMethods();
        methods.addAll(asList(classMethods));
        addMethods(clazz.getSuperclass(), methods);
    }

    protected void addAnnotations(Class<?> clazz, List<Annotation> classAnnotations) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        classAnnotations.addAll(asList(annotations));
        addAnnotations(clazz.getSuperclass(), classAnnotations);
    }
}
