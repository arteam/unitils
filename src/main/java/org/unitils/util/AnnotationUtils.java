/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utilities for retrieving and working with annotations.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class AnnotationUtils {


    /**
     * Returns the given class's declared fields that are marked with the given annotation
     *
     * @param clazz      The class, not null
     * @param annotation The annotation, not null
     * @return A List containing fields annotated with the given annotation, empty list if none found
     */
    public static <T extends Annotation> List<Field> getFieldsAnnotatedWith(Class clazz, Class<T> annotation) {
        if (Object.class.equals(clazz)) {
            return Collections.emptyList();
        }
        List<Field> annotatedFields = new ArrayList<Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.getAnnotation(annotation) != null) {
                annotatedFields.add(field);
            }
        }
        annotatedFields.addAll(getFieldsAnnotatedWith(clazz.getSuperclass(), annotation));
        return annotatedFields;
    }


    /**
     * Returns the given class's (and superclasses) declared methods that are marked with the given annotation
     *
     * @param clazz      The class, not null
     * @param annotation The annotation, not null
     * @return A List containing methods annotated with the given annotation, empty list if none found
     */
    public static <T extends Annotation> List<Method> getMethodsAnnotatedWith(Class clazz, Class<T> annotation) {
        return getMethodsAnnotatedWith(clazz, annotation, true);
    }


    /**
     * Returns the given class's declared methods that are marked with the given annotation
     *
     * @param clazz            The class, not null
     * @param annotation       The annotation, not null
     * @param includeInherited True for also looking for methods in super-classes
     * @return A List containing methods annotated with the given annotation, empty list if none found
     */
    public static <T extends Annotation> List<Method> getMethodsAnnotatedWith(Class clazz, Class<T> annotation, boolean includeInherited) {
        if (Object.class.equals(clazz)) {
            return Collections.emptyList();
        }
        List<Method> annotatedMethods = new ArrayList<Method>();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(annotation) != null) {
                annotatedMethods.add(method);
            }
        }
        if (includeInherited) {
            annotatedMethods.addAll(getMethodsAnnotatedWith(clazz.getSuperclass(), annotation));
        }
        return annotatedMethods;
    }


}
