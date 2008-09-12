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
package org.unitils.core.util;

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
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Kenny Claes
 */
public class CloneUtil {

    private static Objenesis objenesis = new ObjenesisStd();


    @SuppressWarnings({"unchecked"})
    public static <T> T createDeepClone(T object) {
        try {
            return (T) cloneObject(object, new IdentityHashMap<Object, Object>());

        } catch (Throwable e) {
            throw new UnitilsException("Unexpected exception during cloning of " + object, e);
        }
    }

    protected static Object cloneObject(Object instanceToClone, Map<Object, Object> cloneCache) throws Throwable {
        // check for null
        if (instanceToClone == null) {
            return null;
        }
        // check whether the instance was already cloned, this will observe the object graph
        Object clonedInstance = cloneCache.get(instanceToClone);
        if (clonedInstance != null) {
            return clonedInstance;
        }
        // check for immutable values such as primitive values
        clonedInstance = getValueIfImmutable(instanceToClone);
        if (clonedInstance != null) {
            return clonedInstance;
        }

        if (instanceToClone.getClass().isArray()) {
            clonedInstance = cloneArray(instanceToClone, cloneCache);
        }

        // if the instance is cloneable, try to clone it
        if (clonedInstance == null) {
            clonedInstance = createInstanceUsingClone(instanceToClone);
        }

        // try to clone it ourselves
        if (clonedInstance == null) {
            clonedInstance = createInstanceUsingObjenesis(instanceToClone);

            // Unable to create an instance
            if (clonedInstance == null) {
                // todo log warning
                return instanceToClone;
            }
        }

        cloneCache.put(instanceToClone, clonedInstance);
        cloneFields(instanceToClone.getClass(), instanceToClone, clonedInstance, cloneCache);
        return clonedInstance;
    }


    protected static Object getValueIfImmutable(Object instanceToClone) {
        Class<?> clazz = instanceToClone.getClass();

        if (clazz.isPrimitive() || clazz.isEnum() || clazz.isAnnotation()) {
            return instanceToClone;
        }
        if (instanceToClone instanceof Number || instanceToClone instanceof String || instanceToClone instanceof Character || instanceToClone instanceof Boolean) {
            return instanceToClone;
        }
        return null;
    }


    protected static Object createInstanceUsingClone(Object instanceToClone) {
        if (!(instanceToClone instanceof Cloneable)) {
            return null;
        }
        try {
            Method cloneMethod = Object.class.getDeclaredMethod("clone");
            cloneMethod.setAccessible(true);
            return cloneMethod.invoke(instanceToClone);

        } catch (Throwable t) {
            return null;
        }
    }


    protected static Object createInstanceUsingObjenesis(Object instanceToClone) {
        try {
            return objenesis.newInstance(instanceToClone.getClass());

        } catch (Throwable t) {
            return null;
        }
    }


    protected static void cloneFields(Class clazz, Object instanceToClone, Object clonedInstance, Map<Object, Object> cloneCache) throws Throwable {
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


    public static Object cloneArray(Object arrayToClone, Map<Object, Object> cloneCache) throws Throwable {
        // todo add to cache to avoid infinite loops
        int lenght = Array.getLength(arrayToClone);
        Object clonedArray = Array.newInstance(arrayToClone.getClass().getComponentType(), lenght);

        for (int i = 0; i < lenght; i++) {
            Object elementValue = Array.get(arrayToClone, i);
            Object clonedElementValue = cloneObject(elementValue, cloneCache);
            Array.set(clonedArray, i, clonedElementValue);
        }
        return clonedArray;
    }

}