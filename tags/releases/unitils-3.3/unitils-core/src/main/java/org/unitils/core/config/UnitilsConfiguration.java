/*
 * Copyright 2011, Unitils.org
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
package org.unitils.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.UnitilsException;
import org.unitils.core.util.Configurable;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsConfiguration {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(UnitilsConfiguration.class);


    /* All configuration properties, not null */
    private Properties properties;


    /**
     * Creates a configuration for the given properties.
     *
     * @param properties All configuration properties, not null
     */
    public UnitilsConfiguration(Properties properties) {
        this.properties = properties;
    }


    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @return The trimmed string value, not null
     */
    public String getString(String propertyName) {
        String value = properties.getProperty(propertyName);
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
     * @return The trimmed string value, not null
     */
    public String getString(String propertyName, String defaultValue) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return value.trim();
    }


    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @return The trimmed string list, empty if none found
     */
    public List<String> getStringList(String propertyName) {
        return getStringList(propertyName, false);
    }

    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned if not required, else an exception is raised. Empty elements (",,")
     * will not be added.
     *
     * @param propertyName The name, not null
     * @param required     If true an exception will be raised when the property is not found or empty
     * @return The trimmed string list, empty or exception if none found
     */
    public List<String> getStringList(String propertyName, boolean required) {
        String values = properties.getProperty(propertyName);
        if (values == null || "".equals(values.trim())) {
            if (required) {
                throw new UnitilsException("No value found for property " + propertyName);
            }
            return new ArrayList<String>(0);
        }
        String[] splitValues = values.split(",");
        List<String> result = new ArrayList<String>(splitValues.length);
        for (String value : splitValues) {
            if (value == null || "".equals(value.trim())) {
                continue;
            }
            result.add(value.trim());
        }

        if (required && result.isEmpty()) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return result;
    }


    /**
     * Gets the boolean value for the property with the given name. If no such property is found,
     * the value is empty or not a boolean, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @return The boolean value
     */
    public boolean getBoolean(String propertyName) {
        String value = getString(propertyName, null);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        return toBoolean(propertyName, value);
    }

    /**
     * Gets the boolean value for the property with the given name. If no such property is found or
     * the value is empty, the given default value is returned. An exception will be raised if the
     * value is not a boolean.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @return The boolean value
     */
    public boolean getBoolean(String propertyName, boolean defaultValue) {
        String value = getString(propertyName, null);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        return toBoolean(propertyName, value);
    }

    private boolean toBoolean(String propertyName, String value) {
        if ("true".equalsIgnoreCase(value)) {
            return true;
        }
        if ("false".equalsIgnoreCase(value)) {
            return false;
        }
        throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a boolean.");
    }


    /**
     * Gets the long value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to a long, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @return The long value
     */
    public long getLong(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        try {
            return Long.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a long.");
        }
    }

    /**
     * Gets the long value for the property with the given name. If no such property is found or
     * the value is empty, the given default value is returned. An exception will be raised if the
     * value cannot be converted to a long.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @return The long value
     */
    public long getLong(String propertyName, long defaultValue) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        try {
            return Long.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a long.");
        }
    }


    /**
     * Gets the int value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to an int, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @return The int value
     */
    public int getInt(String propertyName) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            throw new UnitilsException("No value found for property " + propertyName);
        }
        try {
            return Integer.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a number.");
        }
    }

    /**
     * Gets the int value for the property with the given name. If no such property is found or
     * the value is empty, the given default value is returned. An exception will be raised if the
     * value cannot be converted to an int.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @return The int value
     */
    public int getInt(String propertyName, int defaultValue) {
        String value = properties.getProperty(propertyName);
        if (value == null || "".equals(value.trim())) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value.trim());

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " for property " + propertyName + " is not a number.");
        }
    }


    /**
     * Gets an instance of the class name specified by the property with the given name. If no such property is found, the
     * value is empty or the instance cannot be created, an exception will be raised.<br/>
     * <br/>
     * If the created instance is implementation of {@link Configurable}, the init method will be called.
     *
     * @param propertyName The name, not null
     * @return The instance value, not null
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getInstance(String propertyName) {
        String className = getString(propertyName);
        return (T) createInstanceOfType(className, false);
    }

    /**
     * Gets an instance of the class name specified by the property with the given name. If no such property is found, the
     * value is empty, the given default value is returned. An exception will be raised if the instance cannot be created.<br/
     * <br/>
     * If the created instance is implementation of {@link Configurable}, the init method will be called.
     *
     * @param propertyName The name, not null
     * @param defaultValue The default value
     * @return The instance value, not null
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getInstance(String propertyName, T defaultValue) {
        String className = getString(propertyName, null);
        if (className == null) {
            return defaultValue;
        }
        return (T) createInstance(className);
    }


    /**
     * Gets an instance of the given type (typically an interface).
     * The class name will be resolved as follows:
     * 'fully qualified name of type'.implClassName(.discriminator1.discriminator2)
     *
     * If discriminators are provided, it will first look for a property using all discriminators, then look for
     * a property without the last discriminator etc. If still no property was found, it will look for the property
     * without discriminators.
     *
     * E.g. suppose you have following properties: <br/>
     * <br/>
     * com.package.MyInterface.implClassName = com.package.MyInterfaceImpl<br/>
     * com.package.MyInterface.implClassName.oracle = com.package.MyOracleInterfaceImpl<br/>
     * <br/>
     * getInstanceOf(MyInterface.class, "oracle", "v9");<br/>
     * <br/>
     * will first try<br/>
     * com.package.MyInterface.implClassName.oracle.v9<br/>
     * <br/>
     * but since no such property exists it will try<br/>
     * com.package.MyInterface.implClassName.oracle<br/>
     * <br/>
     * and since such a property exists, it will return an instance com.package.MyOracleInterfaceImpl<br/>
     * <br/>
     * If oracle did not exist it would have looked for the property without discriminators, i.e.<br/>
     * com.package.MyInterface.implClassName<br/>
     * <br/>
     * If the created instance is implementation of {@link Configurable}, the init method will be called.
     *
     * @param type           The type of the instance
     * @param discriminators Optional. The values that define which specific implementation class should be used.
     * @return The instance
     */
    @SuppressWarnings({"unchecked"})
    public <T> T getInstanceOf(Class<T> type, String... discriminators) {
        String implClassName = getImplClassName(type, discriminators);
        logger.debug("Creating instance of " + type + ". Implementation class " + implClassName);
        return (T) createInstance(implClassName);
    }

    private String getImplClassName(Class<?> type, String... discriminators) {
        String className = null;
        String propertyName = type.getName() + ".implClassName";

        if (discriminators != null) {
            StringBuffer propertyNameWithDiscriminators = new StringBuffer(propertyName);
            for (String discriminator : discriminators) {
                propertyNameWithDiscriminators.append('.');
                propertyNameWithDiscriminators.append(discriminator);
                String classNameForDiscriminator = getString(propertyNameWithDiscriminators.toString(), null);
                if (classNameForDiscriminator != null) {
                    className = classNameForDiscriminator;
                }
            }
        }
        if (className == null) {
            className = getString(propertyName);
        }
        return className;
    }

    @SuppressWarnings({"unchecked"})
    private <T> T createInstance(String className) {
        T instance = (T) createInstanceOfType(className, false);
        if (instance instanceof Configurable) {
            ((Configurable) instance).init(properties);
        }
        return instance;
    }


    /**
     * @param propertyName The property name, not null
     * @return True if the property exists
     */
    public boolean containsProperty(String propertyName) {
        return properties.getProperty(propertyName) != null;
    }

    /**
     * @return All properties, not null
     */
    public Properties getProperties() {
        return properties;
    }
}
