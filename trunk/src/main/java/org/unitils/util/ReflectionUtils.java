/*
 * Copyright 2008,  Unitils.org
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

import static org.apache.commons.lang.StringUtils.capitalize;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility methods that use reflection for instance creation or class inspection.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class ReflectionUtils {


    /**
     * Creates an instance of the class with the given name.
     * The class's no argument constructor is used to create an instance.
     *
     * @param className           The name of the class, not null
     * @param bypassAccessibility If true, no exception is thrown if the parameterless constructor is not public
     * @return An instance of this class
     * @throws UnitilsException if the class could not be found or no instance could be created
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T createInstanceOfType(String className, boolean bypassAccessibility) {
        try {
            Class<?> type = Class.forName(className);
            return (T) createInstanceOfType(type, bypassAccessibility);

        } catch (ClassCastException e) {
            throw new UnitilsException("Class " + className + " is not of expected type.", e);

        } catch (NoClassDefFoundError e) {
            throw new UnitilsException("Unable to load class " + className, e);

        } catch (ClassNotFoundException e) {
            throw new UnitilsException("Class " + className + " not found", e);

        } catch (Exception e) {
            throw new UnitilsException("Error while instantiating class " + className, e);
        }
    }


    /**
     * Creates an instance of the given type
     *
     * @param <T>                 The type of the instance
     * @param type                The type of the instance
     * @param bypassAccessibility If true, no exception is thrown if the parameterless constructor is not public
     * @return An instance of this type
     * @throws UnitilsException If an instance could not be created
     */
    public static <T> T createInstanceOfType(Class<T> type, boolean bypassAccessibility) {
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (bypassAccessibility) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance();

        } catch (Exception e) {
            throw new UnitilsException("Error while trying to create object of class " + type.getName(), e);
        }
    }


    /**
     * Returns the value of the given field (may be private) in the given object
     *
     * @param object The object containing the field, null for static fields
     * @param field  The field, not null
     * @return The value of the given field in the given object
     * @throws UnitilsException if the field could not be accessed
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, Field field) {
        try {
            field.setAccessible(true);
            return (T) field.get(object);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);

        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }


    /**
     * Sets the given value to the given field on the given object
     *
     * @param object The object containing the field, not null
     * @param field  The field, not null
     * @param value  The value for the given field in the given object
     * @throws UnitilsException if the field could not be accessed
     */
    public static void setFieldValue(Object object, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(object, value);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Unable to assign the value to field: " + field.getName() + ". Ensure that this field is of the correct type.", e);

        } catch (IllegalAccessException e) {
            // Cannot occur, since field.accessible has been set to true
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }


    /**
     * Sets the given value to the given field and setters on the given object.
     *
     * @param object        The object containing the field and setters, not null
     * @param fields        The fields, not null
     * @param setterMethods The setter methods, not null
     * @param value         The value for the given field and setters in the given object
     */
    public static void setFieldAndSetterValue(Object object, Set<Field> fields, Set<Method> setterMethods, Object value) {
        for (Field field : fields) {
            try {
                setFieldValue(object, field, value);

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to assign the value to field: " + field.getName() + ". Ensure that this field is of the correct type.", e);
            }
        }
        for (Method method : setterMethods) {
            if (!isSetter(method)) {
                throw new UnitilsException("Method " + method.getName() + " is expected to be a setter method, but is not.");
            }
            try {
                invokeMethod(object, method, value);

            } catch (UnitilsException e) {
                throw new UnitilsException("Unable to invoke method: " + object.getClass().getSimpleName() + "." + method.getName() + ". Ensure that " +
                        "this method has following signature: void myMethod(ValueType value).", e);
            } catch (InvocationTargetException e) {
                throw new UnitilsException("Unable to invoke method: " + object.getClass().getSimpleName() + "." + method.getName() + ". Method " +
                        "has thrown an exception.", e.getCause());
            }
        }
    }


    /**
     * Invokes the given method with the given parameters on the given target object
     *
     * @param target    The object containing the method, not null
     * @param method    The method, not null
     * @param arguments The method arguments
     * @return The result of the invocation, null if void
     * @throws UnitilsException          if the method could not be invoked
     * @throws InvocationTargetException If the called method throwed an exception
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T invokeMethod(Object target, Method method, Object... arguments) throws InvocationTargetException {
        try {
            method.setAccessible(true);
            return (T) method.invoke(target, arguments);

        } catch (ClassCastException e) {
            throw new UnitilsException("Unable to invoke method. Unexpected return type " + method, e);

        } catch (IllegalArgumentException e) {
            throw new UnitilsException("Error while invoking method " + method, e);

        } catch (IllegalAccessException e) {
            throw new UnitilsException("Error while invoking method " + method, e);
        }
    }


    /**
     * Invoke the given method with the given parameters on the given target object. Doesn't throw
     * any checked exception
     *
     * @param target    The object containing the method, not null
     * @param method    The method, not null
     * @param arguments The method arguments
     * @return The result of the invocation, null if void
     * @throws UnitilsException if the method could not be invoked, or the called method throwed an exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethodSilent(Object target, Method method, Object... arguments) {
        try {
            return (T) invokeMethod(target, method, arguments);
        } catch (InvocationTargetException e) {
            throw new UnitilsException(e);
        }
    }


    /**
     * Returns all declared fields of the given class that are assignable from the given type.
     *
     * @param clazz    The class to get fields from, not null
     * @param type     The type, not null
     * @param isStatic True if static fields are to be returned, false for non-static
     * @return A list of Fields, empty list if none found
     */
    public static Set<Field> getFieldsAssignableFrom(Class<?> clazz, Class<?> type, boolean isStatic) {
        Set<Field> fieldsOfType = new HashSet<Field>();
        Set<Field> allFields = getAllFields(clazz);
        for (Field field : allFields) {
            if (field.getType().isAssignableFrom(type) && isStatic(field.getModifiers()) == isStatic) {
                fieldsOfType.add(field);
            }
        }
        return fieldsOfType;
    }


    /**
     * Returns the fields in the given class that have the exact given type. The class's superclasses are also
     * investigated.
     *
     * @param clazz    The class to get the field from, not null
     * @param type     The type, not null
     * @param isStatic True if static fields are to be returned, false for non-static
     * @return The fields with the given type
     */
    public static Set<Field> getFieldsOfType(Class<?> clazz, Class<?> type, boolean isStatic) {
        Set<Field> fields = new HashSet<Field>();
        Set<Field> allFields = getAllFields(clazz);
        for (Field field : allFields) {
            if (field.getType().equals(type) && isStatic == isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }


    /**
     * Returns all declared setter methods of fields of the given class that are assignable from the given type.
     *
     * @param clazz    The class to get setters from, not null
     * @param type     The type, not null
     * @param isStatic True if static setters are to be returned, false for non-static
     * @return A list of Methods, empty list if none found
     */
    public static Set<Method> getSettersAssignableFrom(Class<?> clazz, Class<?> type, boolean isStatic) {
        Set<Method> settersAssignableFrom = new HashSet<Method>();

        Set<Method> allMethods = getAllMethods(clazz);
        for (Method method : allMethods) {
            if (isSetter(method) && method.getParameterTypes()[0].isAssignableFrom(type) && (isStatic == isStatic(method.getModifiers()))) {
                settersAssignableFrom.add(method);
            }
        }
        return settersAssignableFrom;
    }


    /**
     * Returns the setter methods in the given class that have an argument with the exact given type. The class's
     * superclasses are also investigated.
     *
     * @param clazz    The class to get the setter from, not null
     * @param type     The type, not null
     * @param isStatic True if static setters are to be returned, false for non-static
     * @return All setters for an object of the given type
     */
    public static Set<Method> getSettersOfType(Class<?> clazz, Class<?> type, boolean isStatic) {
        Set<Method> settersOfType = new HashSet<Method>();
        Set<Method> allMethods = getAllMethods(clazz);
        for (Method method : allMethods) {
            if (isSetter(method) && method.getParameterTypes()[0].equals(type)
                    && isStatic == isStatic(method.getModifiers())) {
                settersOfType.add(method);
            }
        }
        return settersOfType;
    }


    /**
     * From the given class, returns the setter for the property with the given name and 1 argument. If isStatic == true,
     * a static setter is searched. If no such setter exists in the given class, null is returned
     *
     * @param clazz        The class to get the setter from, not null
     * @param propertyName The name of the property, not null
     * @param isStatic     True if a static setter is to be returned, false for non-static
     * @return The setter method that matches the given parameters, null if not found
     */
    public static Method getSetter(Class<?> clazz, String propertyName, boolean isStatic) {
        String setterName = "set" + capitalize(propertyName);
        Set<Method> allMethods = getAllMethods(clazz);
        for (Method method : allMethods) {
            if (isSetter(method) && setterName.equals(method.getName())
                    && isStatic == isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 1) {
                return method;
            }
        }
        return null;
    }


    /**
     * From the given class, returns the getter for the given propertyname. If isStatic == true,
     * a static getter is searched. If no such getter exists in the given class, null is returned.
     *
     * @param clazz        The class to get the setter from, not null
     * @param propertyName The name of the property, not null
     * @param isStatic     True if a static getter is to be returned, false for non-static
     * @return The getter method that matches the given parameters, or null if no such method exists
     */
    public static Method getGetter(Class<?> clazz, String propertyName, boolean isStatic) {
        String getterName = "get" + capitalize(propertyName);
        return getMethod(clazz, getterName, isStatic);
    }


    /**
     * From the given class, returns the getter for the given setter method. If no such getter exists in the
     * given class, null is returned.
     *
     * @param setter   The setter method, not null
     * @param isStatic True if a static getter is to be returned, false for non-static
     * @return The getter method that matches the given setter, or null if no such method exists
     */
    public static Method getGetter(Method setter, boolean isStatic) {
        if (!isSetter(setter)) {
            return null;
        }
        String getterName = "get" + setter.getName().substring(3);
        return getMethod(setter.getDeclaringClass(), getterName, isStatic);
    }


    /**
     * From the given class, returns the field with the given name. isStatic indicates if it should be a static
     * field or not.
     *
     * @param clazz     The class to get the field from, not null
     * @param fieldName The name, not null
     * @param isStatic  True if a static field is to be returned, false for non-static
     * @return The field that matches the given parameters, or null if no such field exists
     */
    public static Field getFieldWithName(Class<?> clazz, String fieldName, boolean isStatic) {
        if (clazz == null || clazz.equals(Object.class)) {
            return null;
        }

        Field field;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }

        if (field != null && isStatic(field.getModifiers()) == isStatic) {
            return field;
        }
        return getFieldWithName(clazz.getSuperclass(), fieldName, isStatic);
    }


    /**
     * Gets the enum value that has the given name.
     *
     * @param enumClass     The enum class, not null
     * @param enumValueName The name of the enum value, not null
     * @return The actual enum value, not null
     * @throws UnitilsException if no value could be found with the given name
     */
    public static <T extends Enum<?>> T getEnumValue(Class<T> enumClass, String enumValueName) {
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
     * @param method The method to check, not null
     * @return True if the given method is a setter, false otherwise
     */
    public static boolean isSetter(Method method) {
        String methodName = method.getName();
        if (methodName.length() > 3 && methodName.startsWith("set") && method.getParameterTypes().length == 1) {
            String fourthLetter = methodName.substring(3, 4);
            if (fourthLetter.toUpperCase().equals(fourthLetter)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the name of the field for the given setter method. An exception is raised when
     * the field name could not be extracted.
     *
     * @param setterMethod The method, not null
     * @return The field name, not null
     */
    public static String getPropertyName(Method setterMethod) {
        String methodName = setterMethod.getName();
        if (methodName.length() < 4 || !methodName.startsWith("set")) {
            throw new UnitilsException("Unable to get field name for setter method " + setterMethod);
        }
        return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
    }


    /**
     * Gets the class for the given name.
     * An UnitilsException is thrown when the class could not be loaded.
     *
     * @param className The name of the class, not null
     * @return The class, not null
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClassWithName(String className) {
        try {
            return (Class<T>) Class.forName(className);

        } catch (Throwable t) {
            throw new UnitilsException("Could not load class with name " + className, t);
        }
    }


    /**
     * Gets the method with the given name from the given class or one of its super-classes.
     *
     * @param clazz          The class containing the method
     * @param methodName     The name of the method, not null
     * @param isStatic       True for a static method, false for non-static
     * @param parameterTypes The parameter types
     * @return The method, null if no matching method was found
     */
    public static Method getMethod(Class<?> clazz, String methodName, boolean isStatic, Class<?>... parameterTypes) {
        if (clazz == null || clazz.equals(Object.class)) {
            return null;
        }

        Method result;
        try {
            result = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            result = null;
        }
        if (result != null && isStatic(result.getModifiers()) == isStatic) {
            return result;
        }
        return getMethod(clazz.getSuperclass(), methodName, isStatic, parameterTypes);
    }


    /**
     * Gets all methods of the given class and all its super-classes.
     *
     * @param clazz The class
     * @return The methods, not null
     */
    private static Set<Method> getAllMethods(Class<?> clazz) {
        Set<Method> result = new HashSet<Method>();
        if (clazz == null || clazz.equals(Object.class)) {
            return result;
        }

        // add all methods of this class
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isSynthetic() || declaredMethod.isBridge()) {
                // skip methods that were added by the compiler
                continue;
            }
            result.add(declaredMethod);
        }
        // add all methods of the super-classes
        result.addAll(getAllMethods(clazz.getSuperclass()));
        return result;
    }


    /**
     * Gets all fields of the given class and all its super-classes.
     *
     * @param clazz The class
     * @return The fields, not null
     */
    public static Set<Field> getAllFields(Class<?> clazz) {
        Set<Field> result = new HashSet<Field>();
        if (clazz == null || clazz.equals(Object.class)) {
            return result;
        }

        // add all fields of this class
        Field[] declaredFields = clazz.getDeclaredFields();
        result.addAll(asList(declaredFields));
        // add all fields of the super-classes
        result.addAll(getAllFields(clazz.getSuperclass()));
        return result;
    }


    /**
     * Gets the string representation of the method as follows:
     *
     * 'class name'.'method name'()
     *
     * @param method The method, not null
     * @return The string representation, not null
     */
    public static String getSimpleMethodName(Method method) {
        StringBuffer result = new StringBuffer();
        result.append(method.getDeclaringClass().getSimpleName());
        result.append('.');
        result.append(method.getName());
        result.append("()");
        return result.toString();
    }
}
