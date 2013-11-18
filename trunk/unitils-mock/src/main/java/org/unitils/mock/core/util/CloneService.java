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
package org.unitils.mock.core.util;

import org.unitils.core.UnitilsException;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;
import static org.unitils.util.ReflectionUtils.isProxy;

/**
 * Utility class for deep cloning objects.
 * In a deep clone, not only the object itself is cloned, but also all the inner objects.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class CloneService {

    protected ObjectFactory objectFactory;


    public CloneService(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }


    /**
     * Creates a deep clone of the given object. If for some reason, the clone cannot be made, a warning is logged
     * and the object itself will be returned. This is also true for all inner objects. If an inner object
     * cannot be cloned, the object itself is used instead.
     *
     * @param object The object to clone
     * @return The cloned instance
     */
    @SuppressWarnings({"unchecked"})
    public <T> T createDeepClone(T object) {
        try {
            return (T) cloneObject(object, new IdentityHashMap<Object, Object>());

        } catch (Throwable e) {
            throw new UnitilsException("Unexpected exception during cloning of " + object, e);
        }
    }


    /**
     * Actual implementation of the cloning.
     * <p/>
     * It will try several ways to clone the object. First it will look for the simple cases: null, primitives,
     * immutable... If not it will check whether it's an array and clone it using the {@link #cloneArray} method.
     * Finally it will see whether the object is cloneable and the clone method can be used. If not, Objenesis is
     * used to create the instance. The last step is to recursively do the same operation for the inner fields.
     * <p/>
     * An object is cloned once. All created clones are put in a cache and if an object is to be cloned a second time,
     * the cached instance is used. This way the object graph is preserved.
     *
     * @param instanceToClone The instance, not null
     * @param cloneCache      The cached clones, not null
     * @return The clone, the instance to clone if the clone could not be made
     */
    protected Object cloneObject(Object instanceToClone, Map<Object, Object> cloneCache) throws Throwable {
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
            Object clone = createInstanceUsingClone(instanceToClone);
            if (clone != null) {
                return clone;
            }
        }
        // don't clone java classes (unless they are cloneable)
        if (isJdkClass(instanceToClone)) {
            return instanceToClone;
        }
        // don't clone proxies
        if (isProxy(instanceToClone)) {
            return instanceToClone;
        }
        // try to clone it ourselves
        Object clonedInstance = createInstanceUsingObjenesis(instanceToClone);

        // Unable to create an instance
        if (clonedInstance == null) {
            return instanceToClone;
        }
        // cache the clone
        cloneCache.put(instanceToClone, clonedInstance);

        // recursively do the same for all inner fields
        cloneFields(instanceToClone.getClass(), instanceToClone, clonedInstance, cloneCache);
        return clonedInstance;
    }

    /**
     * @param instanceToClone The instance, not null
     * @return True if the instance is immutable, e.g. a primitive
     */
    protected boolean isImmutable(Object instanceToClone) {
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
     * @param instanceToClone The instance, not null
     * @return True if the instance is should not be cloned, e.g. a java lang class or a data source
     */
    protected boolean isJdkClass(Object instanceToClone) {
        String className = instanceToClone.getClass().getName();
        if (className.startsWith("java.")) {
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
    protected Object createInstanceUsingClone(Object instanceToClone) {
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
    protected Object createInstanceUsingObjenesis(Object instanceToClone) {
        try {
            return objectFactory.createWithoutCallingConstructor(instanceToClone.getClass());

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
    protected void cloneFields(Class<?> clazz, Object instanceToClone, Object clonedInstance, Map<Object, Object> cloneCache) throws Throwable {
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
    protected Object cloneArray(Object arrayToClone, Map<Object, Object> cloneCache) throws Throwable {
        int length = Array.getLength(arrayToClone);
        Object clonedArray = Array.newInstance(arrayToClone.getClass().getComponentType(), length);
        // Make sure we put the array in the cache before we start cloning the elements, since the array itself may also
        // be one of the elements, and in this case we want to reuse the same element, to avoid infinite recursion.
        cloneCache.put(arrayToClone, clonedArray);

        for (int i = 0; i < length; i++) {
            Object elementValue = Array.get(arrayToClone, i);
            Object clonedElementValue = cloneObject(elementValue, cloneCache);
            Array.set(clonedArray, i, clonedElementValue);
        }
        return clonedArray;
    }
}