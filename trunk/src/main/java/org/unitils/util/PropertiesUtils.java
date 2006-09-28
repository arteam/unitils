/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * Utility methods related to the class <code>java.lang.Properties</code>
 */
public class PropertiesUtils {

    /**
     * Loads the properties file with the given name from the classpath
     *
     * @param propertiesFileName
     * @return A <code>Properties</code> object
     */
    public static Properties loadPropertiesFromClasspath(String propertiesFileName) {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName);
        if (is == null) {
            throw new RuntimeException("Properties file " + propertiesFileName + " not found");
        }
        Properties prop = new Properties();
        try {
            prop.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading properties " + propertiesFileName, e);
        }
        return prop;
    }

    /**
     * Loads the properties file with the given name from the file system
     *
     * @param propertiesFileName
     * @return A <code>Properties</code> object
     */
    public static Properties loadPropertiesFromFile(String propertiesFileName) {
        Properties prop = new Properties();
        try {
            File file = new File(propertiesFileName);
            if (!file.exists()) {
                throw new RuntimeException("Properties file " + propertiesFileName + " not found");
            }
            prop.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException("Error while loading properties " + propertiesFileName, e);
        }
        return prop;
    }

    /**
     * Creates a new <code>Properties</code> object, created from the given array of key-value pairs.
     * @param keyValuePairs Array consisting of a number of arrays with each time 2 elements, the first being the key,
     *                          the second being the value of a property
     * @return a <code>Properties</code> object, containing the given key-value pairs as properties.
     */
    public static Properties asProperties(String[][] keyValuePairs) {
        Properties result = new Properties();
        for (String[] keyValuePair : keyValuePairs) {
            result.put(keyValuePair[0], keyValuePair[1]);
        }
        return result;
    }

    /**
     * Returns the property value with the given key from the given <code>Properties</code> object. If the <code>
     * Properties</code> object doesn't contain the property, an <code>IllegalArgumentException</code> is thrown
     *
     * @param properties
     * @param key
     * @return The value associated with the given key
     * @throws IllegalArgumentException If the <code>Properties</code> doesn't contain the property with the given key
     */
    public static String getPropertyRejectNull(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Property not found " + key);
        }
        return value;
    }

    /**
     * Returns the int property value with the given key from the given <code>Properties</code> object. If the <code>
     * Properties</code> object doesn't contain the property, an <code>IllegalArgumentException</code> is thrown
     *
     * @param properties
     * @param key
     * @return The value associated with the given key
     * @throws IllegalArgumentException If the <code>Properties</code> doesn't contain the property with the given key
     */
    public static int getIntPropertyRejectNull(Properties properties, String key) {
        String value = getPropertyRejectNull(properties, key);
        try {
            int intValue = Integer.parseInt(value);
            return intValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property " + key + " has value " + value + " which is not a correct integer", e);
        }
    }

    /**
     * Returns the long property value with the given key from the given <code>Properties</code> object. If the <code>
     * Properties</code> object doesn't contain the property, an <code>IllegalArgumentException</code> is thrown
     *
     * @param properties
     * @param key
     * @return The value associated with the given key
     * @throws IllegalArgumentException If the <code>Properties</code> doesn't contain the property with the given key
     */
    public static long getLongPropertyRejectNull(Properties properties, String key) {
        String value = getPropertyRejectNull(properties, key);
        try {
            long longValue = Long.parseLong(value);
            return longValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property " + key + " has value " + value + " which is not a correct integer", e);
        }
    }

    /**
     * Returns the boolean property value with the given key from the given <code>Properties</code> object. If the <code>
     * Properties</code> object doesn't contain the property, an <code>IllegalArgumentException</code> is thrown
     *
     * @param properties
     * @param key
     * @return The value associated with the given key
     * @throws IllegalArgumentException If the <code>Properties</code> doesn't contain the property with the given key
     */
    public static boolean getBooleanPropertyRejectNull(Properties properties, String key) {
        String value = getPropertyRejectNull(properties, key);
        if ("true".equalsIgnoreCase(value) || "on".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value) || "off".equalsIgnoreCase(value)) {
            return false;
        }
        throw new IllegalArgumentException("Property " + key + " is a boolean value and should have one of the values"
                + " [true, on, false, off]");
    }

    public static List<String> getCommaSeperatedStringsRejectNull(Properties properties, String key) {
        List<String> result = new ArrayList<String>();
        String value = getPropertyRejectNull(properties, key);
        StringTokenizer st = new StringTokenizer(value, ",");
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }

    public static Set<String> getPropertyKeysStartingWith(Properties unitilsProperties, String propkeyModuleStart) {
        Set<String> propKeysStartingWith = new HashSet<String>();
        Set keys = unitilsProperties.keySet();
        for (Object key : keys) {
            String propKey = (String) key;
            if (propKey.startsWith(propkeyModuleStart)) {
                propKeysStartingWith.add(propKey);
            }
        }
        return propKeysStartingWith;
    }
}
