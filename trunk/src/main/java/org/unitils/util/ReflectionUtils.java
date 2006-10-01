/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.unitils.util;

import org.apache.commons.lang.StringUtils;
import org.unitils.core.UnitilsException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods that use reflection in some way.
 */
public class ReflectionUtils {

    /**
     * Creates an instance of the class with the given name. The class's no argument constructor is used to create an
     * instance.
     *
     * @param className The name of the class
     * @return An instance of this class
     */
    public static <T> T createInstanceOfType(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> constructor = clazz.getConstructor();

            //noinspection unchecked
            return (T) constructor.newInstance();

        } catch (ClassNotFoundException e) {
            throw new UnitilsException("Class " + className + " not found", e);

        } catch (NoSuchMethodException e) {
            throw new UnitilsException("Class " + className + " does not contain no-argument constructor", e);

        } catch (Exception e) {
            throw new UnitilsException("Error while trying to create object of class " + className, e);
        }
    }

    /**
     * Returns the value of the given field in the given object
     *
     * @param object
     * @param field
     * @return the value of the given field in the given object
     */
    public static Object getFieldValue(Object object, Field field) {
        Object fieldValue;
        try {
            field.setAccessible(true);
            fieldValue = field.get(object);
        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
        return fieldValue;
    }

    /**
     * Sets the given value to the given field on the given object
     *
     * @param object
     * @param field
     * @param value
     */
    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }

    /**
     * Invokes the given method with the given parameters on the given target object
     *
     * @param target
     * @param method
     * @param params
     */
    public static Object invokeMethod(Object target, Method method, Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(target, params);
        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while invoking method " + method, e);
        } catch (InvocationTargetException e) {
            throw new UnitilsException("Error while invoking method " + method, e);
        }
    }

    /**
     * Returns all declared fields of the given class that are assignable from the given type
     *
     * @param clazz
     * @param type
     * @param isStatic
     * @return A List of Fields
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
     * Returns the given class's field that has the exact given type, if it exists, or null otherwise
     *
     * @param clazz
     * @param type
     * @return a field, or null
     */
    public static Field getFieldOfType(Class clazz, Class type, boolean isStatic) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType().equals(type) && isStatic == Modifier.isStatic(field.getModifiers())) {
                return field;
            }
        }
        return null;
    }

    public static Method getSetterOfType(Class clazz, Class type, boolean isStatic) {
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isSetter(method) && method.getParameterTypes()[0].equals(type)
                    && isStatic == Modifier.isStatic(method.getModifiers())) {
                return method;
            }
        }
        return null;
    }

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
     * For each method, check if it can be a setter for an object of the given type. A setter is a method with
     * the following properties:
     * <ul>
     * <li>Method name is > 3 characters long and starts with set</li>
     * <li>The fourth character is in uppercase</li>
     * <li>The method has one parameter, with the type of the property to set</li>
     * </ul>
     *
     * @param method
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

    /**
     * From the given class, returns the setter for the property with the given name and type. If isStatic == true,
     * a static setter is searched. If no such setter exists in the given class, null is returned
     *
     * @param propertyName
     * @param type
     * @param isStatic
     * @return The setter method that matches the given parameters
     */
    public static Method getSetter(String propertyName, Class clazz, Class type, boolean isStatic) {
        String setterName = "set" + StringUtils.capitalize(propertyName);
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (isSetter(method) && setterName.equals(method.getName()) && method.getParameterTypes()[0].isAssignableFrom(type)
                    && isStatic == Modifier.isStatic(method.getModifiers())) {
                return method;
            }
        }
        return null;
    }

    /**
     * From the given class, returns the getter for the given propertyname. If isStatic == true, the getter must be
     * static
     *
     * @param propertyName
     * @param clazz
     * @param isStatic
     * @return The getter method that matches the given parameters, or null if no such method exists
     */
    public static Method getGetter(String propertyName, Class clazz, boolean isStatic) {
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
     * @param fieldName
     * @param clazz
     * @param isStatic
     * @return The field that matches the given parameters, or null if no such field exists
     */
    public static Field getFieldWithName(String fieldName, Class clazz, boolean isStatic) {
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
}
