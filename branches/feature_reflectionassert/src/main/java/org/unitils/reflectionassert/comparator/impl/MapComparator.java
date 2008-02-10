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
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;
import org.unitils.reflectionassert.difference.MapDifference;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MapComparator implements Comparator {


    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        if ((left instanceof Map && right instanceof Map)) {
            return true;
        }
        return false;
    }


    public Difference compare(Object left, Object right, ReflectionComparator reflectionComparator) {
        Map<?, ?> leftMap = (Map<?, ?>) left;
        Map<?, ?> rightMap = (Map<?, ?>) right;

        // Create copy from which we can remove elements.
        Map<Object, Object> rightCopy = new HashMap<Object, Object>(rightMap);

        ReflectionComparator keyReflectionComparator = createRefectionComparator();
        MapDifference difference = new MapDifference("Different elements", left, right);

        for (Map.Entry<?, ?> leftEntry : leftMap.entrySet()) {
            Object leftKey = leftEntry.getKey();
            Object leftValue = leftEntry.getValue();

            boolean found = false;
            Iterator<Map.Entry<Object, Object>> rightIterator = rightCopy.entrySet().iterator();
            while (rightIterator.hasNext()) {
                Map.Entry<Object, Object> rightEntry = rightIterator.next();
                Object rightKey = rightEntry.getKey();
                Object rightValue = rightEntry.getValue();

                // compare keys using strict reflection compare
                boolean isKeyEqual = keyReflectionComparator.isEqual(leftKey, rightKey);
                if (isKeyEqual) {
                    found = true;
                    rightIterator.remove();

                    // compare values
                    Difference elementDifference = reflectionComparator.getAllDifferences(leftValue, rightValue);
                    if (elementDifference != null) {
                        difference.addValueDifference(leftKey, elementDifference);
                    }
                    break;
                }
            }

            if (!found) {
                difference.addValueDifference(leftKey, new Difference("Left element not found in right map", leftValue, null));
            }
        }

        for (Map.Entry<?, ?> rightEntry : rightCopy.entrySet()) {
            Object rightKey = rightEntry.getKey();
            Object rightValue = rightEntry.getValue();
            difference.addValueDifference(rightKey, new Difference("Right element not found in left map", null, rightValue));
        }

        if (difference.getValueDifferences().isEmpty()) {
            return null;
        }
        return difference;
    }
}
