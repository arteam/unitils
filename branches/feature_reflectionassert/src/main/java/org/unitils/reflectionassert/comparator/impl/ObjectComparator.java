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
import org.unitils.reflectionassert.comparator.Comparison;
import org.unitils.reflectionassert.comparator.Difference;

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


    // todo javadoc
    public Difference compare(Comparison comparison) {
        Object left = comparison.getLeft();
        Object right = comparison.getRight();

        if (left == null || right == null) {
            return comparison.invokeNextComparator();
        }

        // check different class type
        Class<?> clazz = left.getClass();
        if (!clazz.equals(right.getClass())) {
            return comparison.createDifference("Different class types. Left: " + clazz + ", right: " + right.getClass());
        }
        // compare all fields of the object using reflection
        return compareFields(left, right, clazz, comparison);
    }


    /**
     * Compares the values of all fields in the given objects by use of reflection.
     *
     * @param left       the left object for the comparison, not null
     * @param right      the right object for the comparison, not null
     * @param clazz      the type of both objects
     * @param comparison the current comparison
     * @return the difference, null if there is no difference
     */
    protected Difference compareFields(Object left, Object right, Class<?> clazz, Comparison comparison) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (Field field : fields) {
            comparison.getFieldStack().push(field.getName());

            // skip transient and static fields
            if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                comparison.getFieldStack().pop();
                continue;
            }
            try {

                // recursively check the value of the fields
                Difference difference = comparison.getInnerDifference(field.get(left), field.get(right));
                if (difference != null) {
                    return difference;
                }

            } catch (IllegalAccessException e) {
                // this can't happen. Would get a Security exception instead
                // throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }
            comparison.getFieldStack().pop();
        }

        // compare fields declared in superclass
        Class<?> superclazz = clazz.getSuperclass();
        while (superclazz != null && !superclazz.getName().startsWith("java.lang")) {
            Difference difference = compareFields(left, right, superclazz, comparison);
            if (difference != null) {
                return difference;
            }
            superclazz = superclazz.getSuperclass();
        }
        return null;
    }
}
