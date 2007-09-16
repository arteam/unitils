/*
 * Copyright 2006-2007,  Unitils.org
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

import org.unitils.core.UnitilsException;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Utilities for working with property files.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class PropertyUtils {


    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param properties   The properties, not null
     * @return The trimmed string value, not null
     */
    public static String getString(String propertyName, Properties properties) {
        String value = getProperty(propertyName, properties);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return value.trim();
    }


    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, the given default value is returned.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @param properties   The properties, not null
     * @return The trimmed string value, not null
     */
    public static String getString(String propertyName, String defaultValue, Properties properties) {
        String value = getProperty(propertyName, properties);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return value.trim();
    }


    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added. A space (", ,") is not
     * empty, a "" will be added.
     *
     * @param propertyName The name, not null
     * @param properties   The properties, not null
     * @return The trimmed string list, empty if none found
     */
    public static List<String> getStringList(String propertyName, Properties properties) {
        return getStringList(propertyName, properties, false);

    }


    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned if not required, else an exception is raised. Empty elements (",,")
     * will not be added. A space (", ,") is not empty, a "" will be added.
     *
     * @param propertyName The name, not null
     * @param properties   The properties, not null
     * @param required     If true an exception will be raised when the property is not found or empty
     * @return The trimmed string list, empty or exception if none found
     */
    public static List<String> getStringList(String propertyName, Properties properties, boolean required) {
        String values = getProperty(propertyName, properties);
        if (values == null || "".equals(values.trim())) {
            if (required) {
                throw new UnitilsException("No value found for property " + propertyName);
            }
            return new ArrayList<String>(0);
        }
        String[] splitValues = values.split(",");
        List<String> result = new ArrayList<String>(splitValues.length);
        for (String value : splitValues) {
            result.add(value.trim());
        }

        if (required && result.isEmpty()) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return result;
    }


    /**
     * Gets the boolean value for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param properties   The properties, not null
     * @return The boolean value, not null
     */
    public static boolean getBoolean(String propertyName, Properties properties) {
        String value = getProperty(propertyName, properties);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return Boolean.valueOf(value.trim());
    }


    /**
     * Gets the boolean value for the property with the given name. If no such property is found or
     * the value is empty, the given default value is returned.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @param properties   The properties, not null
     * @return The boolean value, not null
     */
    public static boolean getBoolean(String propertyName, boolean defaultValue, Properties properties) {
        String value = getProperty(propertyName, properties);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return Boolean.valueOf(value.trim());
    }


    /**
     * Gets the long value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to a long, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param properties   The properties, not null
     * @return The long value, not null
     */
    public static long getLong(String propertyName, Properties properties) {
        String value = getProperty(propertyName, properties);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        try {
            return Long.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a number.");
        }
    }


    /**
     * Gets the long value for the property with the given name. If no such property is found or
     * the value is empty, the given default value is returned. If the value cannot be converted to a long,
     * an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @param properties   The properties, not null
     * @return The string value, not null
     */
    public static long getLong(String propertyName, long defaultValue, Properties properties) {
        String value = getProperty(propertyName, properties);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a number.");
        }
    }


    /**
     * Checks whether the property with the given name exists in the System or in the given properties.
     *
     * @param propertyName The property name, not null
     * @param properties   The properties if not found in System, not null
     * @return True if the property exitsts
     */
    public static boolean containsProperty(String propertyName, Properties properties) {
        return getProperty(propertyName, properties) != null;
    }


    /**
     * Gets an instance of the type specified by the property with the given name. If no such property is found, the
     * value is empty or the instance cannot be created, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param properties   The properties, not null
     * @return The instance value, not null
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getInstance(String propertyName, Properties properties) {
        String className = getString(propertyName, properties);
        return (T) createInstanceOfType(className, false);
    }


    /**
     * Gets an instance of the type specified by the property with the given name. If no such property is found, the
     * value is empty, the given default value is returned. If the instance cannot be created an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @param properties   The properties, not null
     * @return The instance value, not null
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getInstance(String propertyName, T defaultValue, Properties properties) {
        String className = getString(propertyName, null, properties);
        if (className == null) {
            return defaultValue;
        }
        return (T) createInstanceOfType(className, false);
    }


    /**
     * Gets a property from the System or from the given properties.
     *
     * @param propertyName The name of the property, not null
     * @param properties   The properties if not found in System, not null
     * @return The property value, null if not found
     */
    private static String getProperty(String propertyName, Properties properties) {
        String value = System.getProperty(propertyName);
        if (value == null) {
            value = properties.getProperty(propertyName);
        }
        return value;
    }

}
