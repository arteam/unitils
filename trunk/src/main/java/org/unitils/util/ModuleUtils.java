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

import static org.unitils.util.AnnotationUtils.getAnnotationPropertyWithName;
import static org.apache.commons.lang.ClassUtils.getShortClassName;
import org.unitils.core.Module;
import org.unitils.core.UnitilsException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Class containing utility methods for module specific configuration. Contains a method for retrieving all annotation
 * property defaults ({@link #getAnnotationPropertyDefaults(Class, java.util.Properties, Class[])} ). The object that
 * this method returns can later be used to get replace the default placeholder of an annotation property for the
 * default value as configured in the unitils configuration. 
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ModuleUtils {

    /**
     * The default name of the default enum value.
     */
    public static final String DEFAULT_ENUM_VALUE_NAME = "DEFAULT";

    /**
     * Returns an object that represents the default values for the properties of the given annotations and the given
     * module.
     *
     * @param moduleClass The class of the module for which we want the default annotation property values
     * @param configuration The unitils configuration
     * @param annotationClasses The annotations for which we want the default values
     * @return An object that returns the annotation property default values
     */
    public static Map<Class<? extends Annotation>, Map<Method, String>> getAnnotationPropertyDefaults(
            Class<? extends Module> moduleClass, Properties configuration, Class<? extends Annotation>... annotationClasses) {

        Map<Class<? extends Annotation>, Map<Method, String>> result = new HashMap<Class<? extends Annotation>, Map<Method, String>>();

        for (Class<? extends Annotation> annotationClass : annotationClasses) {

            Method[] methods = annotationClass.getDeclaredMethods();
            for (Method method : methods) {

                String propertyName = getShortClassName(moduleClass) + "." + getShortClassName(annotationClass) + "." +
                        method.getName() + ".default";

                if (!PropertyUtils.containsProperty(propertyName, configuration)) {
                    continue;
                }

                Map<Method, String> defaultValueMap = result.get(annotationClass);
                if (defaultValueMap == null) {
                    defaultValueMap = new HashMap<Method, String>();
                    result.put(annotationClass, defaultValueMap);
                }
                String defaultValueName = PropertyUtils.getString(propertyName, configuration);
                defaultValueMap.put(method, defaultValueName);
            }
        }
        return result;
    }


    /**
     * Replaces default enum value with the given default enum value.
     * If enumValue contains the value {@link #DEFAULT_ENUM_VALUE_NAME} the defaultValue will be returned otherwise
     * the enumValue itself will be returned.
     *
     * @param annotation        the annotation, not null
     * @param annotationPropertyName the name of the annotation property
     * @param enumValue         the value to check, not null
     * @param allDefaultValues  the map with values to return in case of a default, not null
     * @return the enumValue or the defaultValue in case of a default
     */
    public static <T extends Enum<?>> T getEnumValueReplaceDefault(Class<? extends Annotation> annotation,
                    String annotationPropertyName, T enumValue, Map<Class<? extends Annotation>, Map<Method, String>> allDefaultValues) {

        return getEnumValueReplaceDefault(annotation, annotationPropertyName, enumValue, allDefaultValues, DEFAULT_ENUM_VALUE_NAME);
    }


    /**
     * Replaces default enum value with the given default enum value.
     * If enumValue contains the value {@link #DEFAULT_ENUM_VALUE_NAME} the defaultValue will be returned otherwise
     * the enumValue itself will be returned.
     *
     * @param annotation        the annotation, not null
     * @param annotationPropertyName the name of the annotation property
     * @param enumValue         the value to check, not null
     * @param allDefaultValues  the map with values to return in case of a default, not null
     * @return the enumValue or the defaultValue in case of a default
     * @param defaultValueName  the name of the default value
     */
    public static <T extends Enum<?>> T getEnumValueReplaceDefault(Class<? extends Annotation> annotation,
                    String annotationPropertyName, T enumValue, Map<Class<? extends Annotation>, Map<Method, String>> allDefaultValues,
                    String defaultValueName) {

        Method annotationProperty = getAnnotationPropertyWithName(annotation, annotationPropertyName);
        String valueAsString = getValueAsStringReplaceDefault(annotation, annotationProperty, enumValue.name(),
                allDefaultValues, defaultValueName);
        //noinspection unchecked
        return (T) ReflectionUtils.getEnumValue(enumValue.getClass(), valueAsString);
    }


    /**
     * Replaces default enum value with the given default enum value.
     * If enumValue contains the value {@link #DEFAULT_ENUM_VALUE_NAME} the defaultValue will be returned otherwise
     * the enumValue itself will be returned.
     *
     * @param annotation        the annotation, not null
     * @param annotationPropertyName the name of the annotation property
     * @param value            the value to check, not null
     * @param allDefaultValues  the map with values to return in case of a default, not null
     * @param defaultValueClass the name of the default value
     * @return the enumValue or the defaultValue in case of a default
     */
    public static Class<?> getClassValueReplaceDefault(Class<? extends Annotation> annotation,
                    String annotationPropertyName, Class<?> value, Map<Class<? extends Annotation>, Map<Method, String>> allDefaultValues,
                    Class<?> defaultValueClass) {

        Method annotationProperty = getAnnotationPropertyWithName(annotation, annotationPropertyName);
        String valueAsString = getValueAsStringReplaceDefault(annotation, annotationProperty, value.getName(),
                allDefaultValues, defaultValueClass.getName());
        return ReflectionUtils.getClassWithName(valueAsString);
    }

    /**
     * Replaces default enum values with the given default enum value.
     * If enumValue contains the value defaultValueName, the defaultValue will be returned otherwise
     * the enumValue itself will be returned.
     *
     * @param annotation         the annotation, not null
     * @param annotationProperty the annotation property for which the value must be replaced, not null
     * @param valueAsString      the value to check, not null
     * @param allDefaultValues   the map with values to return in case of a default, not null
     * @param defaultValueName   the name of the default value, eg DEFAULT, not null @return the enumValue or the defaultValue in case of a default
     * @return The default value as a string
     */
    private static String getValueAsStringReplaceDefault(Class<? extends Annotation> annotation, Method annotationProperty, String valueAsString,
                                                         Map<Class<? extends Annotation>, Map<Method, String>> allDefaultValues, String defaultValueName) {

        if (!defaultValueName.equalsIgnoreCase(valueAsString)) {
            // no replace needed
            return valueAsString;
        }

        Map<Method, String> defaultValues = allDefaultValues.get(annotation);
        if (defaultValues != null) {
            //noinspection SuspiciousMethodCalls
            String defaultValueAsString = defaultValues.get(annotationProperty);
            if (defaultValueAsString != null) {
                //noinspection unchecked
                return defaultValueAsString;
            }
        }
        // nothing found, raise exception
        throw new UnitilsException("Could not replace default value. No default value found for annotation: " + annotation + ", property " + annotationProperty.getName() + ", defaultValues: " + allDefaultValues);
    }

}
