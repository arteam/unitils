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

import org.unitils.reflectionassert.ReflectionComparator;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.difference.ObjectDifference;
import static org.unitils.reflectionassert.util.HibernateUtil.getUnproxiedValue;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import static java.lang.reflect.Modifier.isStatic;
import static java.lang.reflect.Modifier.isTransient;

/**
 * Comparator for objects. This will compare all corresponding field values.
 * <p/>
 * Special thanks to Tim Peeters for helping us with the Hibernate proxy handling.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Tim Peeters
 */
public class ObjectComparator implements Comparator {


    /**
     * Returns true if both objects are not null
     *
     * @param left  The left object
     * @param right The right object
     * @return True if not null
     */
    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        return true;
    }


    /**
     * Compares the given objects by iterating over the fields and comparing the corresponding values.
     * If both objects are of a different type, a difference is returned.
     * The fields of the superclasses are also compared. Fields of java.lang classes are ignored. So for example
     * fields of the Object class are not compared
     *
     * @param left                 The left object, not null
     * @param right                The right object, not null
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return A ObjectDifference or null if both maps are equal
     */
    public Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        // get the actual value if the value is wrapped by a Hibernate proxy
        left = getUnproxiedValue(left);
        right = getUnproxiedValue(right);

        // check different class type
        Class<?> clazz = left.getClass();
        if (!clazz.isAssignableFrom(right.getClass())) {
            return new Difference("Different class types. Left: " + clazz + ", right: " + right.getClass(), left, right);
        }
        // compare all fields of the object using reflection
        ObjectDifference difference = new ObjectDifference("Different field values", left, right);
        compareFields(left, right, clazz, difference, onlyFirstDifference, reflectionComparator);

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
     * @param clazz                the type of the left object, not null
     * @param difference           root difference, not null
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator the reflection comparator, not null
     */
    protected void compareFields(Object left, Object right, Class<?> clazz, ObjectDifference difference, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (Field field : fields) {
            // skip transient and static fields
            if (isTransient(field.getModifiers()) || isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            try {
                // recursively check the value of the fields
                Difference innerDifference = reflectionComparator.getAllDifferences(field.get(left), field.get(right));
                if (innerDifference != null) {
                    difference.addFieldDifference(field.getName(), innerDifference);
                    if (onlyFirstDifference) {
                        return;
                    }
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
            compareFields(left, right, superclazz, difference, onlyFirstDifference, reflectionComparator);
            superclazz = superclazz.getSuperclass();
        }
    }


}
