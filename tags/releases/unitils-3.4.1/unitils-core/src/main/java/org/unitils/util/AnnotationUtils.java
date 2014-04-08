/*
 * Copyright 2008,  Unitils.org
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.unitils.core.UnitilsException;

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
    public static <T extends Annotation> Set<Field> getFieldsAnnotatedWith(Class<? extends Object> clazz, Class<T> annotation) {
        if (Object.class.equals(clazz)) {
            return Collections.emptySet();
        }
        Set<Field> annotatedFields = new HashSet<Field>();
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
    public static <T extends Annotation> Set<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<T> annotation) {
        return getMethodsAnnotatedWith(clazz, annotation, true);
    }
    

    public static <T extends Annotation> Set<T> getMethodLevelAnnotations(Class<?> clazz, Class<T> annotation) {
		Set<T> result = new HashSet<T>();
		Set<Method> annotatedMethods = getMethodsAnnotatedWith(clazz, annotation);
    	for (Method annotatedMethod : annotatedMethods) {
    		result.add(annotatedMethod.getAnnotation(annotation));
    	}
		return result;
	}
	
	
	public static <T extends Annotation> Set<T> getFieldLevelAnnotations(Class<?> clazz, Class<T> annotation) {
		Set<T> result = new HashSet<T>();
		Set<Field> annotatedFields = getFieldsAnnotatedWith(clazz, annotation);
    	for (Field annotatedField : annotatedFields) {
    		result.add(annotatedField.getAnnotation(annotation));
    	}
		return result;
	}


    /**
     * Returns the given class's declared methods that are marked with the given annotation
     *
     * @param clazz            The class, not null
     * @param annotation       The annotation, not null
     * @param includeInherited True for also looking for methods in super-classes
     * @return A List containing methods annotated with the given annotation, empty list if none found
     */
    public static <T extends Annotation> Set<Method> getMethodsAnnotatedWith(Class<?> clazz, Class<T> annotation, boolean includeInherited) {
        if (Object.class.equals(clazz)) {
            return Collections.emptySet();
        }
        Set<Method> annotatedMethods = new HashSet<Method>();
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

    public static <T extends Annotation> T getMethodOrClassLevelAnnotation(Class<T> annotationClass, Method method, Class<?> clazz) {
        T annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        return getClassLevelAnnotation(annotationClass, clazz);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Annotation> T getClassLevelAnnotation(Class<T> annotationClass, Class<?> clazz) {
        if (Object.class.equals(clazz)) {
            return null;
        }

        T annotation = (T) clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            return annotation;
        }
        return getClassLevelAnnotation(annotationClass, clazz.getSuperclass());
    }

    @SuppressWarnings({"unchecked"})
    public static <S extends Annotation, T> T getMethodOrClassLevelAnnotationProperty(Class<S> annotationClass,
                        String annotationPropertyName, T defaultValue, Method method, Class<?> clazz) {

        S annotation = method.getAnnotation(annotationClass);
        if (annotation != null) {
            Method annotationProperty = getAnnotationPropertyWithName(annotationClass, annotationPropertyName);
            T propertyValue = (T) getAnnotationPropertyValue(annotationProperty, annotation);
            if (!defaultValue.equals(propertyValue)) {
                return propertyValue;
            }
        }
        return getClassLevelAnnotationProperty(annotationClass, annotationPropertyName, defaultValue, clazz);
    }


    @SuppressWarnings({"unchecked"})
    public static <S extends Annotation, T> T getClassLevelAnnotationProperty(Class<S> annotationClass,
                         String annotationPropertyName, T defaultValue, Class<?> clazz) {

        if (Object.class.equals(clazz)) {
            return defaultValue;
        }

        S annotation = clazz.getAnnotation(annotationClass);
        if (annotation != null) {
            Method annotationProperty = getAnnotationPropertyWithName(annotationClass, annotationPropertyName);
            T propertyValue = (T) getAnnotationPropertyValue(annotationProperty, annotation);
            if (!defaultValue.equals(propertyValue)) {
                return propertyValue;
            }
        }
        return getClassLevelAnnotationProperty(annotationClass, annotationPropertyName, defaultValue, clazz.getSuperclass());
    }


    public static Method getAnnotationPropertyWithName(Class<? extends Annotation> annotation, String annotationPropertyName) {
        try {
            return annotation.getMethod(annotationPropertyName);
        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Could not find annotation property named " + annotationPropertyName + " on annotation " +
                    annotation.getName());
        }
    }


    @SuppressWarnings("unchecked")
	public static <T> T getAnnotationPropertyValue(Method annotationProperty, Annotation annotation) {
        try {
            return (T) annotationProperty.invoke(annotation);
        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error retrieving value of property " + annotationProperty.getName() +
                " of annotation of type " + annotation.getClass().getSimpleName(), e);
        } catch (InvocationTargetException e) {
            throw new UnitilsException("Error retrieving value of property " + annotationProperty.getName() +
                " of annotation of type " + annotation.getClass().getSimpleName(), e);
        }
    }


	public static boolean hasClassMethodOrFieldLevelAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
		return getClassLevelAnnotation(annotation, clazz) != null || 
				!getFieldsAnnotatedWith(clazz, annotation).isEmpty() ||
				!getMethodsAnnotatedWith(clazz, annotation).isEmpty();
	}

}
