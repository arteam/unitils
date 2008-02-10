/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.reflectionassert.comparator.impl;

import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.difference.ObjectDifference;
import org.unitils.reflectionassert.ReflectionComparator;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectComparator implements Comparator {


    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        return true;
    }


    // todo javadoc
    public Difference compare(Object left, Object right, ReflectionComparator reflectionComparator) {
        // check different class type
        Class<?> clazz = left.getClass();
        if (!clazz.equals(right.getClass())) {
            return new Difference("Different class types. Left: " + clazz + ", right: " + right.getClass(), left, right);
        }
        // compare all fields of the object using reflection
        ObjectDifference difference = new ObjectDifference("Different field values", left, right);
        compareFields(left, right, clazz, difference, reflectionComparator);

        if (difference.getFieldDifferences().isEmpty()) {
            return null;
        }
        return difference;
    }


    /**
     * Compares the values of all fields in the given objects by use of reflection.
     *
     * @param left                 the left object for the comparison, not null
     * @param right                the right object for the comparison, not null
     * @param clazz                the type of both objects, not null
     * @param difference           root difference, not null
     * @param reflectionComparator the reflection comparator, not null
     */
    protected void compareFields(Object left, Object right, Class<?> clazz, ObjectDifference difference, ReflectionComparator reflectionComparator) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (Field field : fields) {
            // skip transient and static fields
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            try {
                // recursively check the value of the fields
                Difference innerDifference = reflectionComparator.getAllDifferences(field.get(left), field.get(right));
                if (innerDifference != null) {
                    difference.addFieldDifference(field.getName(), innerDifference);
                }

            } catch (IllegalAccessException e) {
                // this can't happen. Would get a Security exception instead
                // throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }
        }

        // compare fields declared in superclass
        Class<?> superclazz = clazz.getSuperclass();
        while (superclazz != null && !superclazz.getName().startsWith("java.lang")) {
            compareFields(left, right, superclazz, difference, reflectionComparator);
            superclazz = superclazz.getSuperclass();
        }
    }


}
