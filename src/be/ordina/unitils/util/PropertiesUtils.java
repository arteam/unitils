/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
        InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFileName);
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

}
