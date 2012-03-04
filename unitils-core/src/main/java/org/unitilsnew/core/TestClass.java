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

    protected List<Annotation> classAnnotations;
    protected List<AnnotatedField<?>> annotatedFields;


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

    public List<Annotation> getClassAnnotations() {
        if (classAnnotations != null) {
            return classAnnotations;
        }
        classAnnotations = new ArrayList<Annotation>(3);
        addClassAnnotations(testClass, classAnnotations);
        return classAnnotations;
    }

    public List<AnnotatedField<?>> getAnnotatedFields() {
        if (annotatedFields != null) {
            return annotatedFields;
        }
        annotatedFields = new ArrayList<AnnotatedField<?>>(3);
        addFieldAnnotations(testClass, annotatedFields);
        return annotatedFields;
    }


    public List<Class<? extends Annotation>> getAnnotationTypesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        List<Class<? extends Annotation>> annotationTypes = new ArrayList<Class<? extends Annotation>>();
        List<Annotation> classAnnotations = getClassAnnotations();
        for (Annotation classAnnotation : classAnnotations) {
            Class<? extends Annotation> annotationType = classAnnotation.annotationType();
            if (annotationType.getAnnotation(annotationClass) != null) {
                annotationTypes.add(annotationType);
            }
        }
        List<AnnotatedField<?>> annotatedFields = getAnnotatedFields();
        for (AnnotatedField<?> annotatedField : annotatedFields) {
            Class<? extends Annotation> annotationType = annotatedField.getAnnotation().annotationType();
            if (annotationType.getAnnotation(annotationClass) != null) {
                annotationTypes.add(annotationType);
            }
        }
        return annotationTypes;
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

    public <A extends Annotation> List<AnnotatedField<A>> getAnnotatedFields(Class<A> annotationClass) {
        List<Field> fields = getFields();
        List<A> classAnnotations = getClassAnnotations(annotationClass);
        List<AnnotatedField<A>> annotatedFields = new ArrayList<AnnotatedField<A>>(fields.size());
        for (Field field : fields) {
            A annotation = field.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }
            AnnotatedField<A> annotatedField = new AnnotatedField<A>(field, annotation);
            annotatedFields.add(annotatedField);
        }
        return annotatedFields;
    }

    public <A extends Annotation> List<AnnotatedMethod<A>> getAnnotatedMethods(Class<A> annotationClass) {
        List<Method> methods = getMethods();
        List<AnnotatedMethod<A>> annotatedMethods = new ArrayList<AnnotatedMethod<A>>(methods.size());
        for (Method method : methods) {
            A annotation = method.getAnnotation(annotationClass);
            if (annotation == null) {
                continue;
            }
            AnnotatedMethod<A> annotatedMethod = new AnnotatedMethod<A>(method, annotation);
            annotatedMethods.add(annotatedMethod);
        }
        return annotatedMethods;
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

    protected void addClassAnnotations(Class<?> clazz, List<Annotation> classAnnotations) {
        if (Object.class.equals(clazz)) {
            return;
        }
        Annotation[] annotations = clazz.getDeclaredAnnotations();
        classAnnotations.addAll(asList(annotations));
        addClassAnnotations(clazz.getSuperclass(), classAnnotations);
    }

    protected void addFieldAnnotations(Class<?> testClass, List<AnnotatedField<?>> annotatedFields) {
        List<Field> fields = getFields();
        for (Field field : fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                annotatedFields.add(new AnnotatedField<Annotation>(field, annotation));
            }
        }
    }
}
