/*
 * Copyright (C) 2006, Ordina
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package be.ordina.unitils.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.ArrayList;

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
            throw new IllegalArgumentException("Class " + className + " not found", e);

        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + className + " does not contain no-argument constructor", e);

        } catch (Exception e) {
            throw new RuntimeException("Error while trying to create object of class " + className, e);
        }
    }

    /**
     * Returns the value of the given field in the given object
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
            throw new RuntimeException("Error while trying to access field " + field, e);
        }
        return fieldValue;
    }

    /**
     * Sets the given value to the given field on the given object
     * @param object
     * @param field
     * @param value
     */
    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while trying to access field " + field, e);
        }
    }

    /**
     * Returns the value of the field with the given name in the given object
     * @param object
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     */
    public static Object getFieldValueWithName(Object object, String fieldName) throws NoSuchFieldException {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while accessing field " + fieldName, e);
        }
    }

    /**
     * Invokes the given method with the given parameters on the given target object
     * @param target
     * @param method
     * @param params
     */
    public static void invokeMethod(Object target, Method method, Object... params) {
        try {
            method.setAccessible(true);
            method.invoke(target, params);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error while invoking method " + method, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Error while invoking method " + method, e);
        }
    }

    /**
     * Returns all declared fields of the given class that are assignable from the given type
     * @param clazz
     * @param type
     * @return A List of Fields
     */
    public static List<Field> getFieldsAssignableFrom(Class clazz, Class type) {
        List<Field> fieldsOfType = new ArrayList<Field>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getType().isAssignableFrom(type)) {
                fieldsOfType.add(field);
            }
        }
        return fieldsOfType;
    }

    /**
     * Returns the given class's field that has the exact given type, if it exists, or null otherwise
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
            if (isSetter(method)&& method.getParameterTypes()[0].isAssignableFrom(type)
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
}
