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
import static org.unitils.reflectionassert.comparator.ReflectionComparatorFactory.createRefectionComparator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MapComparator implements Comparator {


    // todo javadoc
    public Difference compare(Comparison comparison) {
        Object left = comparison.getLeft();
        Object right = comparison.getRight();

        if (left == null || right == null) {
            return comparison.invokeNextComparator();
        }
        if (!(left instanceof Map && right instanceof Map)) {
            return comparison.invokeNextComparator();
        }

        Map<?, ?> leftMap = (Map<?, ?>) left;
        Map<?, ?> rightMap = (Map<?, ?>) right;

        if (leftMap.size() != rightMap.size()) {
            return comparison.createDifference("Different map sizes.");
        }

        // Create copy from which we can remove elements.
        Map<Object, Object> rightCopy = new HashMap<Object, Object>(rightMap);

        for (Map.Entry<?, ?> lhsEntry : leftMap.entrySet()) {
            Object lhsKey = lhsEntry.getKey();
            Object lhsValue = lhsEntry.getValue();
            comparison.getFieldStack().push("" + lhsKey);

            boolean found = false;
            Iterator<Map.Entry<Object, Object>> rhsIterator = rightCopy.entrySet().iterator();
            while (rhsIterator.hasNext()) {
                Map.Entry<Object, Object> rhsEntry = rhsIterator.next();
                Object rhsKey = rhsEntry.getKey();
                Object rhsValue = rhsEntry.getValue();

                // compare keys using strict reflection compare
                boolean isKeyEqual = createRefectionComparator().isEqual(lhsKey, rhsKey);
                if (isKeyEqual) {
                    found = true;
                    rhsIterator.remove();

                    // compare values
                    Difference difference = comparison.getInnerDifference(lhsValue, rhsValue);
                    if (difference != null) {
                        return difference;
                    }
                    break;
                }
            }
            comparison.getFieldStack().pop();

            if (!found) {
                return comparison.createDifference("Left key not found in right map. Left key: " + lhsEntry.getKey());
            }
        }
        return null;
    }
}
