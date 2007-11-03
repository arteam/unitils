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
package org.unitils.reflectionassert.comparator;

import org.unitils.reflectionassert.comparator.CollectionComparator;
import org.unitils.reflectionassert.ReflectionComparator;

import java.util.*;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientOrderCollectionComparator extends CollectionComparator {


    // todo javadoc
    public LenientOrderCollectionComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }


    // todo javadoc
    @Override
    protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        // Convert to list and compare as collection
        Collection<?> leftCollection = convertToCollection(left);
        Collection<?> rightCollection = convertToCollection(right);

        if (leftCollection.size() != rightCollection.size()) {
            return new Difference("Different array/collection sizes. Left size: " + leftCollection.size() + ", right size: " + rightCollection.size(), left, right, fieldStack);
        }

        // Create copy from which we can remove elements.
        List<Object> rightCopy = new ArrayList<Object>(rightCollection);

        for (Object lhsValue : leftCollection) {
            boolean found = false;
            Iterator<Object> rhsIterator = rightCopy.iterator();
            while (rhsIterator.hasNext()) {
                Object rhsValue = rhsIterator.next();

                // Compare values using reflection
                Difference difference = rootComparator.getDifference(lhsValue, rhsValue, new Stack<String>(), traversedInstancePairs);
                if (difference == null) {
                    rhsIterator.remove();
                    found = true;
                    break;
                }
            }

            if (!found) {
                return new Difference("Left value not found in right collection/array. Left value: " + lhsValue, left, right, fieldStack);
            }
        }
        return null;
    }

}
