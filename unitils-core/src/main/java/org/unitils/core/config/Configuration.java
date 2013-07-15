/*
 * Copyright 2013,  Unitils.org
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

import org.unitils.core.Factory;
import org.unitils.core.UnitilsException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;

/**
 * todo javadoc
 * <p/>
 * If classifiers are used, it will first look for a property using all classifiers, then look for
 * a property without the last classifier etc. If still no property was found, it will look for the property
 * without discriminators. E.g. suppose the property name is 'key' and there are 2 classifiers 'a' and 'b'. First
 * it will look for a property with name 'key.a.b', if that doesn't exist it will look for 'key.a', and
 * finally it will try 'key'.
 *
 * @author Tim Ducheyne
 */
public class Configuration {

    /* All configuration properties, not null */
    protected Properties properties;

    protected Properties overridingProperties;

    /**
     * Creates a configuration for the given properties.
     *
     * @param properties All configuration properties, not null
     */
    public Configuration(Properties properties) {
        this.properties = properties;
    }


    /**
     * @param propertyName The property name, not null
     * @return True if the property exists
     */
    public boolean containsProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    /**
     * @return All properties, not null
     */
    public Properties getProperties() {
        return properties;
    }

    public Properties getOverridingProperties() {
        return overridingProperties;
    }

    public void setOverridingProperties(Properties overridingProperties) {
        this.overridingProperties = overridingProperties;
    }

    public Properties getAllProperties() {
        Properties result = new Properties();
        result.putAll(properties);
        if (overridingProperties != null) {
            result.putAll(overridingProperties);
        }
        return result;
    }


    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string value, not null
     */
    public String getString(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        if (value == null) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return value;
    }

    /**
     * Gets the string value for the property with the given name. If no such property is found or
     * the value is empty, null is returned.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string value, null if not found
     */
    public String getOptionalString(String propertyName, String... classifiers) {
        String value = getProperty(propertyName);

        if (classifiers != null && classifiers.length > 0) {
            StringBuilder propertyNameWithClassifiers = new StringBuilder(propertyName);
            for (String classifier : classifiers) {
                if (classifier == null) {
                    continue;
                }
                propertyNameWithClassifiers.append('.');
                propertyNameWithClassifiers.append(classifier.trim());

                String valueForClassifier = getProperty(propertyNameWithClassifiers.toString());
                if (valueForClassifier != null) {
                    value = valueForClassifier;
                }
            }
        }

        if (value == null) {
            return null;
        }

        value = value.trim();
        if ("".equals(value)) {
            return null;
        }
        return value;
    }

    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string list, not null
     */
    public List<String> getStringList(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        List<String> result = toStringList(value);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated string values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string list, empty if not found
     */
    public List<String> getOptionalStringList(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toStringList(value);
    }


    /**
     * Gets the boolean value for the property with the given name. If no such property is found,
     * the value is empty or not a boolean, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The boolean value, not null
     */
    public Boolean getBoolean(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toBoolean(value, propertyName, classifiers);
    }

    /**
     * Gets the boolean value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the
     * value is not a boolean.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The boolean value, null if not found
     */
    public Boolean getOptionalBoolean(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toBoolean(value, propertyName, classifiers);
    }

    /**
     * Gets the list of comma separated boolean values for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The boolean list, not null
     */
    public List<Boolean> getBooleanList(String propertyName, String... classifiers) {
        List<Boolean> result = getOptionalBooleanList(propertyName, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated boolean values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The boolean list, empty if not found
     */
    public List<Boolean> getOptionalBooleanList(String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<Boolean> result = new ArrayList<Boolean>(values.size());
        for (String value : values) {
            Boolean bool = toBoolean(value, propertyName, classifiers);
            result.add(bool);
        }
        return result;
    }


    /**
     * Gets the int value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to an int, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The int value
     */
    public Integer getInteger(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toInteger(value, propertyName, classifiers);
    }

    /**
     * Gets the int value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the
     * value cannot be converted to an int.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The int value, null if not found
     */
    public Integer getOptionalInteger(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toInteger(value, propertyName, classifiers);
    }

    /**
     * Gets the list of comma separated int values for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The int list, not null
     */
    public List<Integer> getIntegerList(String propertyName, String... classifiers) {
        List<Integer> result = getOptionalIntegerList(propertyName, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated int values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The int list, empty if not found
     */
    public List<Integer> getOptionalIntegerList(String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<Integer> result = new ArrayList<Integer>(values.size());
        for (String value : values) {
            Integer integer = toInteger(value, propertyName, classifiers);
            result.add(integer);
        }
        return result;
    }


    /**
     * Gets the long value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to a long, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The long value, not null
     */
    public Long getLong(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toLong(value, propertyName, classifiers);
    }

    /**
     * Gets the long value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the
     * value cannot be converted to a long.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The long value, null if not found
     */
    public Long getOptionalLong(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toLong(value, propertyName, classifiers);
    }

    /**
     * Gets the list of comma separated long values for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed long list, not null
     */
    public List<Long> getLongList(String propertyName, String... classifiers) {
        List<Long> result = getOptionalLongList(propertyName, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated long values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The long list, empty if not found
     */
    public List<Long> getOptionalLongList(String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<Long> result = new ArrayList<Long>(values.size());
        for (String value : values) {
            Long longValue = toLong(value, propertyName, classifiers);
            result.add(longValue);
        }
        return result;
    }


    /**
     * Gets the class value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to a class, an exception will be raised.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The class value, not null
     */
    public Class<?> getClass(String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toClass(value, propertyName, classifiers);
    }

    /**
     * Gets the class value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the value cannot be converted to a class.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The class value, null if not found
     */
    public Class<?> getOptionalClass(String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toClass(value, propertyName, classifiers);
    }

    /**
     * Gets the list of comma separated class values for the property with the given name. If no such property is found,
     * the value is empty or cannot be converted to a class, an exception will be raised.
     * Empty elements (",,") will not be added.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The class list, not null
     */
    public List<Class<?>> getClassList(String propertyName, String... classifiers) {
        List<Class<?>> result = getOptionalClassList(propertyName, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated class values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     * An exception will be raised if the value cannot be converted to a class.
     *
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The class list, empty if not found
     */
    public List<Class<?>> getOptionalClassList(String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<Class<?>> result = new ArrayList<Class<?>>(values.size());
        for (String value : values) {
            Class<?> clazz = toClass(value, propertyName, classifiers);
            result.add(clazz);
        }
        return result;
    }


    /**
     * Gets an instance of the given type (typically an interface).
     * It will look for a property using the classname and classifiers and create an instance of the classname
     * specified as value.<br/>
     * E.g. if you have following property:<br/>
     * <br/>
     * org.package.Reader=org.package.MyReaderImpl<br/>
     * <br/>
     * Calling getInstanceOf(Reader.class) will then return an instance of MyReaderImpl
     *
     * @param type        The type of the instance
     * @param classifiers An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance
     */
    public <T> T getInstanceOf(Class<T> type, String... classifiers) {
        String propertyName = type.getName();
        String value = getString(propertyName, classifiers);
        return toInstance(type, value, propertyName, classifiers);
    }

    /**
     * Gets an instance of the given type (typically an interface).
     * It will look for a property using the classname and classifiers and create an instance of the classname
     * specified as value.<br/>
     * E.g. if you have following property:<br/>
     * <br/>
     * org.package.Reader=org.package.MyReaderImpl<br/>
     * <br/>
     * Calling getInstanceOf(Reader.class) will then return an instance of MyReaderImpl
     *
     * @param type        The type of the instance
     * @param classifiers An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance
     */
    public <T> T getOptionalInstanceOf(Class<T> type, String... classifiers) {
        String propertyName = type.getName();
        String value = getOptionalString(type.getName(), classifiers);
        return toInstance(type, value, propertyName, classifiers);
    }

    /**
     * Gets the list of comma separated instances of the given type (typically an interface).
     * It will look for a property using the classname and classifiers and create an instance of the classname
     * specified as value.<br/>
     * E.g. if you have following property:<br/>
     * <br/>
     * org.package.Reader=org.package.MyReaderImpl, org.package.OtherReaderImpl<br/>
     * <br/>
     * Calling getInstanceOf(Reader.class) will then return an instance of MyReaderImpl and OtherReaderImpl<br/>
     * <br/>
     * If no such property is found or the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param type        The type of the instance
     * @param classifiers An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The instance list, not null
     */
    public <T> List<T> getInstanceOfList(Class<T> type, String... classifiers) {
        List<T> result = getOptionalInstanceOfList(type, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(type.getName(), classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated instances of the given type (typically an interface).
     * It will look for a property using the classname and classifiers and create an instance of the classname
     * specified as value.<br/>
     * E.g. if you have following property:<br/>
     * <br/>
     * org.package.Reader=org.package.MyReaderImpl, org.package.OtherReaderImpl<br/>
     * <br/>
     * Calling getInstanceOf(Reader.class) will then return an instance of MyReaderImpl and OtherReaderImpl<br/>
     * <br/>
     * If no such property is found or the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param type        The type of the instance
     * @param classifiers An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The trimmed string list, empty if not found
     */
    public <T> List<T> getOptionalInstanceOfList(Class<T> type, String... classifiers) {
        String propertyName = type.getName();
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<T> result = new ArrayList<T>(values.size());
        for (String value : values) {
            T instance = toInstance(type, value, propertyName, classifiers);
            result.add(instance);
        }
        return result;
    }


    /**
     * Gets the enum value for the property with the given name. If no such property is found, the value is empty
     * or cannot be converted to the given enum type, an exception will be raised.
     *
     * @param type         The enum type, not null
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The enum value, not null
     */
    public <T extends Enum<T>> T getEnumValue(Class<T> type, String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toEnum(type, value, propertyName, classifiers);
    }

    /**
     * Gets the enum value for the property with the given name. If no such property is found or
     * the value is empty, null is returned. An exception will be raised if the value cannot be converted to a the given enum type.
     *
     * @param type         The enum type, not null
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The enum value, null if not found
     */
    public <T extends Enum<T>> T getOptionalEnumValue(Class<T> type, String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toEnum(type, value, propertyName, classifiers);
    }

    /**
     * Gets the list of comma separated enum values for the property with the given name. If no such property is found or
     * the value is empty, an exception will be raised. Empty elements (",,") will not be added.
     *
     * @param type         The enum type, not null
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The enum list, not null
     */
    public <T extends Enum<T>> List<T> getEnumList(Class<T> type, String propertyName, String... classifiers) {
        List<T> result = getOptionalEnumList(type, propertyName, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    /**
     * Gets the list of comma separated enum values for the property with the given name. If no such property is found or
     * the value is empty, an empty list is returned. Empty elements (",,") will not be added.
     *
     * @param type         The enum type, not null
     * @param propertyName The name, not null
     * @param classifiers  An optional list of classifiers for the property name (see class javadoc for more info)
     * @return The enum list, empty if not found
     */
    public <T extends Enum<T>> List<T> getOptionalEnumList(Class<T> type, String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<T> result = new ArrayList<T>(values.size());
        for (String value : values) {
            T enumValue = toEnum(type, value, propertyName, classifiers);
            result.add(enumValue);
        }
        return result;
    }

    // todo javadoc
    public <T> T getValueOfType(Class<T> type, String propertyName, String... classifiers) {
        String value = getString(propertyName, classifiers);
        return toValueOfType(type, value, propertyName, classifiers);
    }

    public <T> T getOptionalValueOfType(Class<T> type, String propertyName, String... classifiers) {
        String value = getOptionalString(propertyName, classifiers);
        return toValueOfType(type, value, propertyName, classifiers);
    }

    public <T> List<T> getValueListOfType(Class<T> type, String propertyName, String... classifiers) {
        List<T> result = getOptionalValueListOfType(type, propertyName, classifiers);
        if (result.isEmpty()) {
            throw new UnitilsException("No value found for " + nameToString(propertyName, classifiers));
        }
        return result;
    }

    public <T> List<T> getOptionalValueListOfType(Class<T> type, String propertyName, String... classifiers) {
        List<String> values = getOptionalStringList(propertyName, classifiers);

        List<T> result = new ArrayList<T>(values.size());
        for (String value : values) {
            T valueOfType = toValueOfType(type, value, propertyName, classifiers);
            result.add(valueOfType);
        }
        return result;
    }


    protected String nameToString(String propertyName, String... classifiers) {
        if (classifiers == null || classifiers.length == 0) {
            return "property " + propertyName;
        }
        return "property " + propertyName + " and classifiers " + Arrays.toString(classifiers);
    }

    protected String getProperty(String propertyName) {
        if (overridingProperties != null && overridingProperties.containsKey(propertyName)) {
            return overridingProperties.getProperty(propertyName);
        }
        return properties.getProperty(propertyName);
    }

    protected Boolean toBoolean(String value, String propertyName, String... classifiers) {
        if (value == null) {
            return null;
        }
        if ("true".equalsIgnoreCase(value)) {
            return TRUE;
        }
        if ("false".equalsIgnoreCase(value)) {
            return FALSE;
        }
        throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a boolean.");
    }

    protected Integer toInteger(String value, String propertyName, String... classifiers) {
        try {
            if (value == null) {
                return null;
            }
            return Integer.valueOf(value);

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not an int.");
        }
    }

    protected Long toLong(String value, String propertyName, String... classifiers) {
        try {
            if (value == null) {
                return null;
            }
            return Long.valueOf(value);

        } catch (NumberFormatException e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a long.");
        }
    }

    protected List<String> toStringList(String value) {
        if (value == null) {
            return new ArrayList<String>(0);
        }
        String[] splitValues = value.split(",");
        List<String> result = new ArrayList<String>(splitValues.length);
        for (String splitValue : splitValues) {
            splitValue = splitValue.trim();
            if ("".equals(splitValue)) {
                continue;
            }
            result.add(splitValue);
        }
        return result;
    }

    protected <T extends Enum<T>> T toEnum(Class<T> type, String value, String propertyName, String[] classifiers) {
        try {
            if (value == null) {
                return null;
            }
            return Enum.valueOf(type, value);
        } catch (Exception e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a valid enum value for type " + type.getName(), e);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected <T> T toInstance(String value, String propertyName, String... classifiers) {
        if (value == null) {
            return null;
        }
        try {
            T instance = (T) createInstanceOfType(value, true);
            if (instance instanceof Factory) {
                return (T) ((Factory) instance).create();
            }
            return instance;
        } catch (Exception e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a valid classname.", e);
        }
    }

    protected Class<?> toClass(String value, String propertyName, String[] classifiers) {
        if (value == null) {
            return null;
        }
        try {
            return Class.forName(value);
        } catch (Exception e) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not a valid class name.", e);
        }
    }

    @SuppressWarnings({"unchecked"})
    protected <T> T toInstance(Class<T> type, String value, String propertyName, String... classifiers) {
        T instance = (T) toInstance(value, propertyName, classifiers);
        if (instance != null && !type.isAssignableFrom(instance.getClass())) {
            throw new UnitilsException("Value " + value + " of " + nameToString(propertyName, classifiers) + " is not of the expected type " + type.getName());
        }
        return instance;
    }

    @SuppressWarnings({"unchecked"})
    protected <T> T toValueOfType(Class<T> type, String value, String propertyName, String... classifiers) {
        if (type.isAssignableFrom(String.class)) {
            return (T) value;
        }
        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            return (T) toBoolean(value, propertyName, classifiers);
        }
        if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            return (T) toInteger(value, propertyName, classifiers);
        }
        if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            return (T) toLong(value, propertyName, classifiers);
        }
        if (type.isEnum()) {
            return (T) toEnum((Class<Enum>) type, value, propertyName, classifiers);
        }
        if (Class.class.equals(type)) {
            return (T) toClass(value, propertyName, classifiers);
        }
        return toInstance(type, value, propertyName, classifiers);
    }
}
