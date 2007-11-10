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

import org.apache.commons.lang.ArrayUtils;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.comparator.Comparison;
import org.unitils.reflectionassert.comparator.Difference;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CollectionComparator implements Comparator {


    // todo javadoc
    public Difference compare(Comparison comparison) {
        Object left = comparison.getLeft();
        Object right = comparison.getRight();

        if (left == null || right == null) {
            return comparison.invokeNextComparator();
        }
        if (!(left.getClass().isArray() || left instanceof Collection) || !(right.getClass().isArray() || right instanceof Collection)) {
            return comparison.invokeNextComparator();
        }

        // Convert to list and compare as collection
        Collection<?> leftCollection = convertToCollection(left);
        Collection<?> rightCollection = convertToCollection(right);

        if (leftCollection.size() != rightCollection.size()) {
            return comparison.createDifference("Different array/collection sizes. Left size: " + leftCollection.size() + ", right size: " + rightCollection.size());
        }

        int i = 0;
        Iterator<?> lhsIterator = leftCollection.iterator();
        Iterator<?> rhsIterator = rightCollection.iterator();
        while (lhsIterator.hasNext() && rhsIterator.hasNext()) {
            comparison.getFieldStack().push("" + i++);
            Difference difference = comparison.getInnerDifference(lhsIterator.next(), rhsIterator.next());
            if (difference != null) {
                return difference;
            }
            comparison.getFieldStack().pop();
        }
        return null;
    }


    /**
     * Converts the given array or collection object (possibly primitive array) to type Collection
     *
     * @param object the array or collection
     * @return the object collection
     */
    protected Collection<?> convertToCollection(Object object) {
        if (object instanceof Collection<?>) {
            return (Collection<?>) object;
        }

        // If needed convert primitive array to object array
        Object[] objectArray = convertToObjectArray(object);

        // Convert array to collection
        return Arrays.asList(objectArray);
    }


    /**
     * Converts the given array object (possibly primitive array) to type Object[]
     *
     * @param object the array
     * @return the object array
     */
    protected Object[] convertToObjectArray(Object object) {
        if (object instanceof byte[]) {
            return ArrayUtils.toObject((byte[]) object);

        } else if (object instanceof short[]) {
            return ArrayUtils.toObject((short[]) object);

        } else if (object instanceof int[]) {
            return ArrayUtils.toObject((int[]) object);

        } else if (object instanceof long[]) {
            return ArrayUtils.toObject((long[]) object);

        } else if (object instanceof char[]) {
            return ArrayUtils.toObject((char[]) object);

        } else if (object instanceof float[]) {
            return ArrayUtils.toObject((float[]) object);

        } else if (object instanceof double[]) {
            return ArrayUtils.toObject((double[]) object);

        } else if (object instanceof boolean[]) {
            return ArrayUtils.toObject((boolean[]) object);

        } else {
            return (Object[]) object;
        }
    }
}
