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
package org.unitils.mock.core.proxy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static java.lang.reflect.Modifier.isStatic;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Utility class for deep cloning objects.
 * In a deep clone, not only the object itself is cloned, but also all the inner objects.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class CloneUtil {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(CloneUtil.class);

    /* Objenesis instance for creating new instances of types */
    private static Objenesis objenesis = new ObjenesisStd();


    /**
     * Creates a deep clone of the given object. If for some reason, the clone cannot be made, a warning is logged
     * and the object itself will be returned. This is also true for all inner objects. If an inner object
     * cannot be cloned, the object itself is used instead.
     *
     * @param object The object to clone
     * @return The cloned instance
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T createDeepClone(T object) {
        try {
            return (T) cloneObject(object, new IdentityHashMap<Object, Object>());

        } catch (Throwable e) {
            throw new UnitilsException("Unexpected exception during cloning of " + object, e);
        }
    }


    /**
     * Actual implementation of the cloning.
     *
     * It will try several ways to clone the object. First it will look for the simple cases: null, primitives,
     * immutables... If not it will check whether it's an array and clone it using the {@link #cloneArray} method.
     * Finally it will see whether the object is cloneable and the clone method can be used. If not, Objenisis is
     * used to create the instance. The last step is to recursively do the same operation for the inner fields.
     *
     * An object is cloned once. All created clones are put in a cache and if an object is to be cloned a second time,
     * the cached instance is used. This way the object graph is preserved.
     *
     * @param instanceToClone The instance, not null
     * @param cloneCache      The cached clones, not null
     * @return The clone, the instance to clone if the clone could not be made
     */
    protected static Object cloneObject(Object instanceToClone, Map<Object, Object> cloneCache) throws Throwable {
        if (instanceToClone == null) {
            return null;
        }
        // check whether the instance was already cloned, this will preserve the object graph
        if (cloneCache.containsKey(instanceToClone)) {
            return cloneCache.get(instanceToClone);
        }
        // if the value is immutable, return the instance itself
        if (isImmutable(instanceToClone)) {
            return instanceToClone;
        }
        // check for arrays
        if (instanceToClone.getClass().isArray()) {
            return cloneArray(instanceToClone, cloneCache);
        }
        // if the instance is cloneable, try to clone it
        if (instanceToClone instanceof Cloneable) {
            return createInstanceUsingClone(instanceToClone);
        }
        // try to clone it ourselves
        Object clonedInstance = createInstanceUsingObjenesis(instanceToClone);

        // Unable to create an instance
        if (clonedInstance == null) {
            logger.warn("Could not create an instance of class " + instanceToClone.getClass() + " using objenesis");
            return instanceToClone;
        }
        // cache the clone
        cloneCache.put(instanceToClone, clonedInstance);

        // recursively do the same for all inner fields
        cloneFields(instanceToClone.getClass(), instanceToClone, clonedInstance, cloneCache);
        return clonedInstance;
    }


    /**
     * Returns the given value if it is immutable, else null is returned.
     *
     * @param instanceToClone The instance, not null
     * @return The instance if it is immutable, else null
     */
    protected static boolean isImmutable(Object instanceToClone) {
        Class<?> clazz = instanceToClone.getClass();

        if (clazz.isPrimitive() || clazz.isEnum() || clazz.isAnnotation()) {
            return true;
        }
        if (instanceToClone instanceof Number || instanceToClone instanceof String || instanceToClone instanceof Character || instanceToClone instanceof Boolean) {
            return true;
        }
        return false;
    }


    /**
     * If the given value is cloneable and the cloning succeeds, the clone is returned, else null is returned.
     *
     * @param instanceToClone The instance, not null
     * @return The clone if it could be cloned, else null
     */
    protected static Object createInstanceUsingClone(Object instanceToClone) {
        try {
            Method cloneMethod = Object.class.getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            return cloneMethod.invoke(instanceToClone);

        } catch (Throwable t) {
            return null;
        }
    }


    /**
     * Tries to create an instance of the same type as the given value using Objenesis.
     *
     * @param instanceToClone The instance, not null
     * @return The new instance if it could be created, else null
     */
    protected static Object createInstanceUsingObjenesis(Object instanceToClone) {
        try {
            return objenesis.newInstance(instanceToClone.getClass());

        } catch (Throwable t) {
            return null;
        }
    }


    /**
     * Clones all values in all fields of the given class and superclasses.
     *
     * @param clazz           The current class
     * @param instanceToClone The instance, not null
     * @param clonedInstance  The clone, not null
     * @param cloneCache      The cached clones, not null
     */
    protected static void cloneFields(Class<?> clazz, Object instanceToClone, Object clonedInstance, Map<Object, Object> cloneCache) throws Throwable {
        if (clazz == null || Object.class.equals(clazz)) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (Field field : fields) {
            // skip static fields
            if (isStatic(field.getModifiers())) {
                continue;
            }

            Object fieldValue = field.get(instanceToClone);
            Object clonedFieldValue = cloneObject(fieldValue, cloneCache);
            field.set(clonedInstance, clonedFieldValue);
        }
        cloneFields(clazz.getSuperclass(), instanceToClone, clonedInstance, cloneCache);
    }


    /**
     * Clones the given array and all it's elements.
     *
     * @param arrayToClone The array, not null
     * @param cloneCache   The cached clones, not null
     * @return The cloned array, not null
     */
    protected static Object cloneArray(Object arrayToClone, Map<Object, Object> cloneCache) throws Throwable {
        int lenght = Array.getLength(arrayToClone);
        Object clonedArray = Array.newInstance(arrayToClone.getClass().getComponentType(), lenght);
        // Make sure we put the array in the cache before we start cloning the elements, since the array itself may also
        // be one of the elements, and in this case we want to reuse the same element, to avoid infinite recursion.
        cloneCache.put(arrayToClone, clonedArray);

        for (int i = 0; i < lenght; i++) {
            Object elementValue = Array.get(arrayToClone, i);
            Object clonedElementValue = cloneObject(elementValue, cloneCache);
            Array.set(clonedArray, i, clonedElementValue);
        }
        return clonedArray;
    }

}