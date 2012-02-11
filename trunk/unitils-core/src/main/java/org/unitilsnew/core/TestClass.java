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

import org.unitilsnew.core.config.Configuration;

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

    protected Configuration configuration;


    public TestClass(Class<?> testClass, Configuration configuration) {
        this.testClass = testClass;
        this.configuration = configuration;
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

    /**
     * todo javadoc
     * Returns the declared fields of the test class and its superclasses that are marked with the given annotation
     *
     * @param annotationClass The annotation type, not null
     * @return A List containing fields annotated with the given annotation, empty list if none found
     */
    public <A extends Annotation> List<A> getClassAnnotations(Class<A> annotationClass) {
        List<A> classAnnotations = new ArrayList<A>(3);
        Class<?> clazz = testClass;
        while (!Object.class.equals(clazz)) {
            A annotation = clazz.getAnnotation(annotationClass);
            if (annotation != null) {
                classAnnotations.add(annotation);
            }
            clazz = clazz.getSuperclass();
        }
        return classAnnotations;
    }

    /**
     * todo javadoc
     * Returns the declared fields of the test class and its superclasses that are marked with the given annotation
     *
     * @param annotationClass The annotation type, not null
     * @return A List containing fields annotated with the given annotation, empty list if none found
     */
    public <A extends Annotation> List<FieldAnnotation<A>> getFieldAnnotations(Class<A> annotationClass) {
        List<Field> fields = getFields();
        List<A> classAnnotations = getClassAnnotations(annotationClass);
        List<FieldAnnotation<A>> fieldAnnotations = new ArrayList<FieldAnnotation<A>>(fields.size());
        for (Field field : fields) {
            A annotation = field.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }
            FieldAnnotation<A> fieldAnnotation = new FieldAnnotation<A>(field, annotation, classAnnotations, configuration);
            fieldAnnotations.add(fieldAnnotation);
        }
        return fieldAnnotations;
    }

    /**
     * todo javadoc
     * Returns the given class's (and superclasses) declared methods that are marked with the given annotation
     *
     * @param annotationClass The annotation type, not null
     * @return A List containing methods annotated with the given annotation, empty list if none found
     */
    public <A extends Annotation> List<MethodAnnotation<A>> getMethodAnnotations(Class<A> annotationClass) {
        List<Method> methods = getMethods();
        List<MethodAnnotation<A>> methodAnnotations = new ArrayList<MethodAnnotation<A>>(methods.size());
        for (Method method : methods) {
            A annotation = method.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }
            MethodAnnotation<A> methodAnnotation = new MethodAnnotation<A>(method, annotation);
            methodAnnotations.add(methodAnnotation);
        }
        return methodAnnotations;
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
}
