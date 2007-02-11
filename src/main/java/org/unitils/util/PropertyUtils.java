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

import org.unitils.core.UnitilsException;

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


    //todo javadoc
    public static String getString(String propertyName, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return value.trim();
    }


    //todo javadoc
    public static String getString(String propertyName, String defaultValue, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return value.trim();
    }


    //todo javadoc
    public static List<String> getStringList(String propertyName, Properties properties) {
        String values = properties.getProperty(propertyName);
        if (values == null || "".equals(values.trim())) {
            return new ArrayList<String>(0);
        }
        String[] splitValues = values.split(",");
        List<String> result = new ArrayList<String>(splitValues.length);
        for (String value : splitValues) {
            result.add(value.trim());
        }
        return result;
    }


    //todo javadoc
    public static boolean getBoolean(String propertyName, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return Boolean.valueOf(value.trim());
    }


    //todo javadoc
    public static boolean getBoolean(String propertyName, boolean defaultValue, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return Boolean.valueOf(value.trim());
    }


    //todo javadoc
    public static long getLong(String propertyName, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        try {
            return Long.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a number.");
        }
    }


    //todo javadoc
    public static long getLong(String propertyName, long defaultValue, Properties properties) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a number.");
        }
    }
}
