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

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods that use reflection for instance creation or class inspection.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtils {


    /**
     * The default name of the default enum value.
     */
    public static final String DEFAULT_ENUM_VALUE_NAME = "DEFAULT";


    /**
     * Creates an instance of the class with the given name.
     * The class's no argument constructor is used to create an instance.
     *
     * @param className the name of the class, not null
     * @return an instance of this class
     * @throws UnitilsException if the class could not be found or no instance could be created
     */
    public static <T> T createInstanceOfType(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();

            //noinspection unchecked
            return (T) constructor.newInstance();

        } catch (NoClassDefFoundError e) {
            throw new UnitilsException("Unable to load class " + className, e);

        } catch (ClassNotFoundException e) {
            throw new UnitilsException("Class " + className + " not found", e);

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Class " + className + " does not contain no-argument constructor", e);

        } catch (Exception e) {
            e.printStackTrace();
            throw new UnitilsException("Error while trying to create object of class " + className, e);
        }
    }

    /**
     * Returns the value of the given field (may be private) in the given object
     *
     * @param object the object containing the field, null for static fields
     * @param field  the field, not null
     * @return the value of the given field in the given object
     * @throws UnitilsException if the field could not be accessed
     */
    public static Object getFieldValue(Object object, Field field) {
        Object fieldValue;
        try {
            field.setAccessible(true);
            fieldValue = field.get(object);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);

        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
        return fieldValue;
    }

    /**
     * Sets the given value to the given field on the given object
     *
     * @param object the object containing the field, not null
     * @param field  the field, not null
     * @param value  the value for the given field in the given object
     * @throws UnitilsException if the field could not be accessed
     */
    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);

        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }

    /**
     * Invokes the given method with the given parameters on the given target object
     *
     * @param target    the object containing the method, not null
     * @param method    the method, not null
     * @param arguments the method arguments
     * @return the result of the invocation, null if void
     * @throws UnitilsException if the method could not be invoked
     * @throws InvocationTargetException If the called method throwed an exception
     */
    public static <T> T invokeMethod(Object target, Method method, Object... arguments) throws InvocationTargetException {
        try {
            method.setAccessible(true);
            return (T) method.invoke(target, arguments);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Error while invoking method " + method, e);
        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while invoking method " + method, e);
        }
    }

    /**
     * Returns all declared fields of the given class that are assignable from the given type.
     *
     * @param clazz    the class to get fields from, not null
     * @param type     the type, not null
     * @param isStatic true if static fields are to be returned, false for non-static
     * @return a List of Fields, empty list if none found
     */
    public static List<Field> getFieldsAssignableFrom(Class clazz, Class type, boolean isStatic) {

        List<Field> fieldsOfType = new ArrayList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType().isAssignableFrom(type) && Modifier.isStatic(field.getModifiers()) == isStatic) {
                fieldsOfType.add(field);
            }
        }
        return fieldsOfType;
    }

    /**
     * Returns the fields in the given class that have the exact given type. The class's superclasses are also
     * investigated.
     *
     * @param clazz    the class to get the field from, not null
     * @param type     the type, not null
     * @param isStatic true if static fields are to be returned, false for non-static
     * @return The fields with the given type
     */
    public static List<Field> getFieldsOfType(Class clazz, Class type, boolean isStatic) {
        List<Field> fields = getFieldsOfTypeIgnoreSuper(clazz, type, isStatic);
        Class superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            fields.addAll(getFieldsOfType(superClass, type, isStatic));
        }
        return fields;
    }

    /**
     * Returns the fields in the given class that have the exact given type. The class's superclasses are not
     * investigated.
     *
     * @param clazz
     * @param type
     * @param isStatic
     * @return The fields with the given type
     */
    private static List<Field> getFieldsOfTypeIgnoreSuper(Class clazz, Class type, boolean isStatic) {
        List<Field> fields = new ArrayList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType().equals(type) && isStatic == Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }


    /**
     * Returns all declared setter methods of fields of the given class that are assignable from the given type.
     *
     * @param clazz    the class to get setters from, not null
     * @param type     the type, not null
     * @param isStatic true if static setters are to be returned, false for non-static
     * @return a List of Methods, empty list if none found
     */
    public static List<Method> getSettersAssignableFrom(Class clazz, Class type, boolean isStatic) {
        List<Method> settersAssignableFrom = new ArrayList<Method>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isSetter(method) && method.getParameterTypes()[0].isAssignableFrom(type)
                    && isStatic == Modifier.isStatic(method.getModifiers())) {
                settersAssignableFrom.add(method);
            }
        }
        return settersAssignableFrom;
    }


    /**
     * Returns the setter methods in the given class that have an argument with the exact given type. The class's
     * superclasses are also investigated.
     *
     * @param clazz    the class to get the setter from, not null
     * @param type     the type, not null
     * @param isStatic true if static setters are to be returned, false for non-static
     * @return All setters for an object of the given type
     */
    public static List<Method> getSettersOfType(Class clazz, Class type, boolean isStatic) {
        List<Method> setters = getSettersOfTypeIgnoreSuper(clazz, type, isStatic);
        Class superClass = clazz.getSuperclass();
        if (!superClass.equals(Object.class)) {
            setters.addAll(getSettersOfType(superClass, type, isStatic));
        }
        return setters;
    }

    /**
     * Returns the setter methods in the given class that have an argument with the exact given type. The class's
     * superclasses are not investigated.
     *
     * @param clazz    the class to get the setter from, not null
     * @param type     the type, not null
     * @param isStatic true if static setters are to be returned, false for non-static
     * @return All setters for an object of the given type
     */
    private static List<Method> getSettersOfTypeIgnoreSuper(Class clazz, Class type, boolean isStatic) {
        List<Method> settersOfType = new ArrayList<Method>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isSetter(method) && method.getParameterTypes()[0].equals(type)
                    && isStatic == Modifier.isStatic(method.getModifiers())) {
                settersOfType.add(method);
            }
        }
        return settersOfType;
    }


    /**
     * From the given class, returns the setter for the property with the given name and 1 argument. If isStatic == true,
     * a static setter is searched. If no such setter exists in the given class, null is returned
     *
     * @param clazz        the class to get the setter from, not null
     * @param propertyName the name of the property, not null
     * @param isStatic     true if a static setter is to be returned, false for non-static
     * @return the setter method that matches the given parameters, null if not found
     */
    public static Method getSetter(Class clazz, String propertyName, boolean isStatic) {
        String setterName = "set" + StringUtils.capitalize(propertyName);
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isSetter(method) && setterName.equals(method.getName())
                    && isStatic == Modifier.isStatic(method.getModifiers())) {
                return method;
            }
        }
        return null;
    }

    /**
     * From the given class, returns the getter for the given propertyname. If isStatic == true,
     * a static getter is searched. If no such getter exists in the given class, null is returned.
     *
     * @param clazz        the class to get the setter from, not null
     * @param propertyName the name of the property, not null
     * @param isStatic     true if a static getter is to be returned, false for non-static
     * @return The getter method that matches the given parameters, or null if no such method exists
     */
    public static Method getGetter(Class clazz, String propertyName, boolean isStatic) {
        String getterName = "get" + StringUtils.capitalize(propertyName);
        try {
            Method getter = clazz.getDeclaredMethod(getterName);
            if (isStatic == Modifier.isStatic(getter.getModifiers())) {
                return getter;
            } else {
                return null;
            }
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * From the given class, returns the field with the given name. isStatic indicates if it should be a static
     * field or not.
     *
     * @param clazz     the class to get the field from, not null
     * @param fieldName the name, not null
     * @param isStatic  true if a static field is to be returned, false for non-static
     * @return the field that matches the given parameters, or null if no such field exists
     */
    public static Field getFieldWithName(Class clazz, String fieldName, boolean isStatic) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (Modifier.isStatic(field.getModifiers()) == isStatic) {
                return field;
            } else {
                return null;
            }
        } catch (NoSuchFieldException e) {
            return null;
        }
    }


    /**
     * Gets the enum value that has the given name.
     *
     * @param enumClass     the enum class, not null
     * @param enumValueName the name of the enum value, not null
     * @return the actual enum value, not null
     * @throws UnitilsException if no value could be found with the given name
     */
    public static <T extends Enum> T getEnumValue(Class<T> enumClass, String enumValueName) {

        T[] enumValues = enumClass.getEnumConstants();
        for (T enumValue : enumValues) {
            if (enumValueName.equalsIgnoreCase(enumValue.name())) {

                return enumValue;
            }
        }
        throw new UnitilsException("Unable to find a enum value in enum: " + enumClass + ", with value name: " + enumValueName);
    }


    /**
     * For each method, check if it can be a setter for an object of the given type. A setter is a method with
     * the following properties:
     * <ul>
     * <li>Method name is > 3 characters long and starts with set</li>
     * <li>The fourth character is in uppercase</li>
     * <li>The method has one parameter, with the type of the property to set</li>
     * </ul>
     *
     * @param method the method to check, not null
     * @return true if the given method is a setter, false otherwise
     */
    private static boolean isSetter(Method method) {
        String methodName = method.getName();
        if (methodName.length() > 3 && method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
            String fourthLetter = methodName.substring(3, 4);
            if (fourthLetter.toUpperCase().equals(fourthLetter)) {
                return true;
            }
        }
        return false;
    }

}
