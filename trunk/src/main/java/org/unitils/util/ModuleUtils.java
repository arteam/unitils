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

import static org.apache.commons.lang.ClassUtils.getShortClassName;
import org.unitils.core.Module;
import org.unitils.core.UnitilsException;
import static org.unitils.util.PropertyUtils.getString;
import static org.unitils.util.ReflectionUtils.getEnumValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ModuleUtils {

    /**
     * The default name of the default enum value.
     */
    public static final String DEFAULT_ENUM_VALUE_NAME = "DEFAULT";


    //todo javadoc
    @SuppressWarnings({"unchecked"})
    public static Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> getAnnotationEnumDefaults(Class<? extends Module> moduleClass, Properties configuration, Class<? extends Annotation>... annotationClasses) {

        Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> result = new HashMap<Class<? extends Annotation>, Map<Class<Enum>, Enum>>();

        for (Class<? extends Annotation> annotationClass : annotationClasses) {

            String moduleClassName = getShortClassName(moduleClass);
            String annotationClassName = getShortClassName(annotationClass);

            Method[] methods = annotationClass.getDeclaredMethods();
            for (Method method : methods) {

                Class<?> returnType = method.getReturnType();
                if (returnType.isEnum()) {

                    Class<Enum> enumClass = (Class<Enum>) returnType;
                    String enumClassName = getShortClassName(enumClass);
                    String propertyName = moduleClassName + "." + annotationClassName + "." + enumClassName + ".default";

                    if (!configuration.containsKey(propertyName)) {
                        continue;
                    }
                    String defaultEnumValueName = getString(propertyName, configuration);

                    Enum defaultEnumValue = getEnumValue(enumClass, defaultEnumValueName);
                    if (defaultEnumValue == null) {
                        continue;
                    }

                    Map<Class<Enum>, Enum> enumMap = result.get(annotationClass);
                    if (enumMap == null) {

                        enumMap = new HashMap<Class<Enum>, Enum>();
                        result.put(annotationClass, enumMap);
                    }
                    enumMap.put(enumClass, defaultEnumValue);
                }
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
     * @param enumValue         the value to check, not null
     * @param defaultEnumValues the map with values to return in case of a default, not null
     * @return the enumValue or the defaultValue in case of a default
     */
    public static <T extends Enum> T getValueReplaceDefault(Class<? extends Annotation> annotation, T enumValue, Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> defaultEnumValues) {
        return getValueReplaceDefault(annotation, enumValue, defaultEnumValues, DEFAULT_ENUM_VALUE_NAME);
    }

    /**
     * Replaces default enum values with the given default enum value.
     * If enumValue contains the value defaultValueName, the defaultValue will be returned otherwise
     * the enumValue itself will be returned.
     *
     * @param annotation        the annotation, not null
     * @param enumValue         the value to check, not null
     * @param defaultEnumValues the map with values to return in case of a default, not null
     * @param defaultValueName  the name of the default value, eg DEFAULT, not null
     * @return the enumValue or the defaultValue in case of a default
     */
    public static <T extends Enum> T getValueReplaceDefault(Class<? extends Annotation> annotation, T enumValue, Map<Class<? extends Annotation>, Map<Class<Enum>, Enum>> defaultEnumValues, String defaultValueName) {
        if (!defaultValueName.equalsIgnoreCase(enumValue.name())) {
            // no replace needed
            return enumValue;
        }

        Map<Class<Enum>, Enum> defaultValues = defaultEnumValues.get(annotation);
        if (defaultValues != null) {
            //noinspection SuspiciousMethodCalls
            Enum defaultValue = defaultValues.get(enumValue.getClass());
            if (defaultValue != null) {
                //noinspection unchecked
                return (T) defaultValue;
            }
        }
        // nothing found raise exception
        throw new UnitilsException("Could not replace default value. No default value found for annotation: " + annotation + ", enum: " + enumValue.getClass() + ", defaultEnumValues: " + defaultEnumValues);
    }

}
