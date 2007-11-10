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

import org.unitils.reflectionassert.comparator.Comparison;
import org.unitils.reflectionassert.comparator.Difference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientOrderCollectionComparator extends CollectionComparator {


    // todo javadoc
    @Override
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

        // Create copy from which we can remove elements.
        List<Object> rightCopy = new ArrayList<Object>(rightCollection);

        for (Object lhsValue : leftCollection) {
            boolean found = false;
            Iterator<Object> rhsIterator = rightCopy.iterator();
            while (rhsIterator.hasNext()) {
                Object rhsValue = rhsIterator.next();

                // Compare values using reflection
                Difference difference = comparison.getNewDifference(lhsValue, rhsValue);
                if (difference == null) {
                    rhsIterator.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                return comparison.createDifference("Left value not found in right collection/array. Left value: " + lhsValue);
            }
        }
        return null;
    }

}
