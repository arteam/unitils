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
package org.unitils.core.util;

import org.unitils.core.UnitilsException;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;

import static java.lang.reflect.Modifier.isStatic;
import static java.util.Arrays.asList;
import static org.springframework.util.ClassUtils.isCglibProxyClassName;

/**
 * Utility methods that use reflection for instance creation or class
 * inspection.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtils {

    /**
     * Creates an instance of the class with the given name. The class's no
     * argument constructor is used to create an instance.
     *
     * @param className           The name of the class, not null
     * @param bypassAccessibility If true, no exception is thrown if the no-parameter constructor is not public
     * @return An instance of this class
     * @throws UnitilsException if the class could not be found or no instance could be
     *                          created
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T createInstanceOfType(String className, boolean bypassAccessibility) {
        Class<T> type;
        try {
            type = (Class<T>) Class.forName(className);

        } catch (Exception e) {
            throw new UnitilsException("Unable to create instance of type " + className, e);
        }
        return createInstanceOfType(type, bypassAccessibility);
    }

    /**
     * Creates an instance of the given type
     *
     * @param <T>                 The type of the instance
     * @param type                The type of the instance
     * @param bypassAccessibility If true, no exception is thrown if the default constructor is not public
     * @return An instance of this type
     * @throws UnitilsException If an instance could not be created
     */
    public static <T> T createInstanceOfType(Class<T> type, boolean bypassAccessibility) {
        return createInstanceOfType(type, bypassAccessibility, new Class[0], new Object[0]);
    }

    /**
     * Creates an instance of the given type
     *
     * @param <T>                 The type of the instance
     * @param type                The type of the instance
     * @param bypassAccessibility If true, no exception is thrown if the default constructor is not public
     * @param argumentTypes       The constructor arg types, not null
     * @param arguments           The constructor args, not null
     * @return An instance of this type
     * @throws UnitilsException If an instance could not be created
     */
    public static <T> T createInstanceOfType(Class<T> type, boolean bypassAccessibility, Class[] argumentTypes, Object[] arguments) {
        if (type.isMemberClass() && !isStatic(type.getModifiers())) {
            throw new UnitilsException(
                    "Creation of an instance of a non-static innerclass is not possible using reflection. The type "
                            + type.getSimpleName() + " is only known in the context of an instance of the enclosing class "
                            + type.getEnclosingClass().getSimpleName() + ". Declare the innerclass as static to make construction possible.");
        }
        try {
            Constructor<T> constructor = type.getDeclaredConstructor(argumentTypes);
            if (bypassAccessibility) {
                constructor.setAccessible(true);
            }
            return constructor.newInstance(arguments);

        } catch (InvocationTargetException e) {
            throw new UnitilsException("Error while trying to create object of class " + type.getName(), e.getCause());

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

        } catch (Exception e) {
            throw new UnitilsException("Error while trying to access field " + field, e);
        }
    }

    /**
     * Invokes the given method with the given parameters on the given target
     * object
     *
     * @param target    The object containing the method, not null
     * @param method    The method, not null
     * @param arguments The method arguments
     * @return The result of the invocation, null if void
     * @throws UnitilsException if the method could not be invoked
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T invokeMethod(Object target, Method method, Object... arguments) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(target, arguments);

        } catch (InvocationTargetException e) {
            throw new UnitilsException("Error while invoking method " + method, e.getCause());

        } catch (Exception e) {
            throw new UnitilsException("Error while invoking method " + method, e);
        }
    }

    /**
     * Gets the class for the given name. An UnitilsException is thrown when the
     * class could not be loaded.
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
     * <p/>
     * 'class name'.'method name'()
     *
     * @param method The method, not null
     * @return The string representation, not null
     */
    public static String getSimpleMethodName(Method method) {
        return method.getDeclaringClass().getSimpleName() + '.' + method.getName() + "()";
    }

    /**
     * Checks whether the given fromType is assignable to the given toType, also
     * taking into account possible auto-boxing.
     *
     * @param fromType The from type, not null
     * @param toType   The to type, not null
     * @return True if assignable
     */
    public static boolean isAssignable(Type fromType, Type toType) {
        if (fromType instanceof Class<?> && toType instanceof Class<?>) {
            Class<?> fromClass = (Class<?>) fromType;
            Class<?> toClass = (Class<?>) toType;

            // handle auto boxing types
            if (byte.class.equals(fromClass) && Byte.class.equals(toClass) || byte.class.equals(toClass) && Byte.class.equals(fromClass)) {
                return true;
            }
            if (boolean.class.equals(fromClass) && Boolean.class.equals(toClass) || boolean.class.equals(toClass) && Boolean.class.equals(fromClass)) {
                return true;
            }
            if (char.class.equals(fromClass) && Character.class.equals(toClass) || char.class.equals(toClass) && Character.class.equals(fromClass)) {
                return true;
            }
            if (int.class.equals(fromClass) && Integer.class.equals(toClass) || int.class.equals(toClass) && Integer.class.equals(fromClass)) {
                return true;
            }
            if (long.class.equals(fromClass) && Long.class.equals(toClass) || long.class.equals(toClass) && Long.class.equals(fromClass)) {
                return true;
            }
            if (float.class.equals(fromClass) && Float.class.equals(toClass) || float.class.equals(toClass) && Float.class.equals(fromClass)) {
                return true;
            }
            if (double.class.equals(fromClass) && Double.class.equals(toClass) || double.class.equals(toClass) && Double.class.equals(fromClass)) {
                return true;
            }
            return toClass.isAssignableFrom(fromClass);
        }
        return TypeUtils.isAssignable(toType, fromType);
    }

    /**
     * Gets the T from a Class<T> type declaration. An exception is raised if
     * the type has more than 1 generic type
     *
     * @param type The type to get the generic type parameter from, not null
     * @return The declared generic type parameter, null if not generic a generic type
     */
    public static Class<?> getGenericParameterClass(Type type) {
        Type parameterType = getGenericParameterType(type);
        if (parameterType instanceof Class) {
            return (Class<?>) parameterType;
        }
        return null;
    }

    /**
     * Gets the T from a Class<T> type declaration. An exception is raised if
     * the type has more than 1 generic type
     *
     * @param type The type to get the generic type parameter from, not null
     * @return The declared generic type parameter, null if not generic a generic type
     */
    public static Type getGenericParameterType(Type type) {
        if (!(type instanceof ParameterizedType) || ((ParameterizedType) type).getActualTypeArguments().length == 0) {
            return null;
        }
        Type[] argumentTypes = ((ParameterizedType) type).getActualTypeArguments();
        if (argumentTypes.length > 1) {
            throw new UnitilsException("Unable to determine unique generic type for type: " + type + ". The type declares more than one generic type: " + type);
        }
        Type argumentType = argumentTypes[0];
        if (argumentType instanceof ParameterizedType) {
            argumentType = ((ParameterizedType) argumentType).getRawType();
        }
        return argumentType;
    }

    public static void copyFields(Object fromObject, Object toObject) throws IllegalAccessException {
        copyFields(fromObject.getClass(), fromObject, toObject);
    }

    public static Class<?> getTestClass(Object testInstanceOrClass) {
        if (testInstanceOrClass == null) {
            return null;
        }
        if (testInstanceOrClass instanceof Class) {
            return (Class) testInstanceOrClass;
        }
        return testInstanceOrClass.getClass();
    }

    /**
     * @param instance The instance to check, not null
     * @return True if the given instance is a jdk or cglib proxy
     */
    public static boolean isProxy(Object instance) {
        if (instance == null) {
            return false;
        }
        Class<?> clazz = instance.getClass();
        return isCglibProxyClassName(clazz.getName()) || Proxy.isProxyClass(clazz);
    }


    protected static void copyFields(Class<?> clazz, Object fromObject, Object toObject) throws IllegalAccessException {
        if (clazz == null) {
            return;
        }
        for (Field field : clazz.getDeclaredFields()) {
            // don't copy static and special fields (e.g. EMMA adds fields for calculating coverage that can't be copied)
            if (field.isSynthetic() || isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);
            Object fromValue = field.get(fromObject);
            field.set(toObject, fromValue);
        }
        copyFields(clazz.getSuperclass(), fromObject, toObject);
    }
}