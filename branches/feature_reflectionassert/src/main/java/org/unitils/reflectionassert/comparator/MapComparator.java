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

import org.unitils.reflectionassert.ReflectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorChainFactory.STRICT_COMPARATOR;
import org.unitils.reflectionassert.util.Difference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MapComparator extends ReflectionComparator {


    // todo javadoc
    public MapComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }

    // todo javadoc
    @Override
    public Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        if (left == null || right == null) {
            return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
        }
        if (!(left instanceof Map && right instanceof Map)) {
            return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
        }

        Map<?, ?> leftMap = (Map<?, ?>) left;
        Map<?, ?> rightMap = (Map<?, ?>) right;

        if (leftMap.size() != rightMap.size()) {
            return new Difference("Different map sizes.", left, right, fieldStack);
        }

        // Create copy from which we can remove elements.
        Map<Object, Object> rightCopy = new HashMap<Object, Object>(rightMap);

        for (Map.Entry<?, ?> lhsEntry : leftMap.entrySet()) {
            Object lhsKey = lhsEntry.getKey();
            Object lhsValue = lhsEntry.getValue();
            fieldStack.push("" + lhsKey);

            boolean found = false;
            Iterator<Map.Entry<Object, Object>> rhsIterator = rightCopy.entrySet().iterator();
            while (rhsIterator.hasNext()) {
                Map.Entry<Object, Object> rhsEntry = rhsIterator.next();
                Object rhsKey = rhsEntry.getKey();
                Object rhsValue = rhsEntry.getValue();

                // compare keys using strict reflection compare
                boolean isKeyEqual = STRICT_COMPARATOR.isEqual(lhsKey, rhsKey);
                if (isKeyEqual) {
                    found = true;
                    rhsIterator.remove();

                    // compare values
                    Difference difference = rootComparator.getDifference(lhsValue, rhsValue, fieldStack, traversedInstancePairs);
                    if (difference != null) {
                        return difference;
                    }
                    break;
                }
            }
            fieldStack.pop();

            if (!found) {
                return new Difference("Left key not found in right map. Left key: " + lhsEntry.getKey(), left, right, fieldStack);
            }
        }
        return null;
    }
}
