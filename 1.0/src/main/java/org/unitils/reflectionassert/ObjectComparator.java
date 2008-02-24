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
package org.unitils.reflectionassert;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Stack;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ObjectComparator extends ReflectionComparator {


    // todo javadoc
    public ObjectComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }


    // todo javadoc
    public boolean canHandle(Object left, Object right) {
        return left != null && right != null;
    }


    // todo javadoc
    @Override
    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        // check different class type
        Class<?> clazz = left.getClass();
        if (!clazz.equals(right.getClass())) {
            return new Difference("Different class types. Left: " + clazz + ", right: " + right.getClass(), left, right, fieldStack);
        }
        // compare all fields of the object using reflection
        return compareFields(left, right, clazz, fieldStack, traversedInstancePairs);
    }


    /**
     * Compares the values of all fields in the given objects by use of reflection.
     *
     * @param left                   the left object for the comparison, not null
     * @param right                  the right object for the comparison, not null
     * @param clazz                  the type of both objects
     * @param fieldStack             the current field names
     * @param traversedInstancePairs Map with pairs of objects that have been compared with each other.
     * @return the difference, null if there is no difference
     */
    protected Difference compareFields(Object left, Object right, Class<?> clazz, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);

        for (Field f : fields) {
            fieldStack.push(f.getName());

            // skip transient and static fields
            if (Modifier.isTransient(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) {
                fieldStack.pop();
                continue;
            }
            try {

                // recursively check the value of the fields
                Difference difference = rootComparator.getDifference(f.get(left), f.get(right), fieldStack, traversedInstancePairs);
                if (difference != null) {
                    return difference;
                }

            } catch (IllegalAccessException e) {
                // this can't happen. Would get a Security exception instead
                // throw a runtime exception in case the impossible happens.
                throw new InternalError("Unexpected IllegalAccessException");
            }
            fieldStack.pop();
        }

        // compare fields declared in superclass
        Class<?> superclazz = clazz.getSuperclass();
        while (superclazz != null && !superclazz.getName().startsWith("java.lang")) {
            Difference difference = compareFields(left, right, superclazz, fieldStack, traversedInstancePairs);
            if (difference != null) {
                return difference;
            }
            superclazz = superclazz.getSuperclass();
        }
        return null;
    }
}
