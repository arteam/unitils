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
import org.unitils.reflectionassert.util.Difference;

import java.util.Map;
import java.util.Stack;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientNumberComparator extends ReflectionComparator {


    // todo javadoc
    public LenientNumberComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }


    // todo javadoc
    @Override
    public Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        if (left == null || right == null) {
            return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
        }
        if (!(left instanceof Character || left instanceof Number) || !(right instanceof Character || right instanceof Number)) {
            return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
        }
        // check if right and left have same number value (including NaN and Infinity)
        Double leftDouble = getDoubleValue(left);
        Double rightDouble = getDoubleValue(right);
        if (leftDouble.equals(rightDouble)) {
            return null;
        }
        return new Difference("Different primitive values.", left, right, fieldStack);
    }


    /**
     * Gets the double value for the given left Character or Number instance.
     *
     * @param object the Character or Number, not null
     * @return the value as a Double (this way NaN and infinity can be compared)
     */
    private Double getDoubleValue(Object object) {
        if (object instanceof Number) {
            return ((Number) object).doubleValue();
        }
        return (double) ((Character) object).charValue();
    }
}
