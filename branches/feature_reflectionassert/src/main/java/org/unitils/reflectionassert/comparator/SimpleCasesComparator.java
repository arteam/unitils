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
public class SimpleCasesComparator extends ReflectionComparator {


    // todo javadoc
    public SimpleCasesComparator(ReflectionComparator chainedComparator) {
        super(chainedComparator);
    }


    // todo javadoc
    @Override
    public Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Map<TraversedInstancePair, Boolean> traversedInstancePairs) {
        // check if the same instance is referenced
        if (left == right) {
            return null;
        }
        // check if the left value is null
        if (left == null) {
            return new Difference("Left value null.", left, right, fieldStack);
        }
        // check if the right value is null
        if (right == null) {
            return new Difference("Right value null.", left, right, fieldStack);
        }
        // check if right and left have same number value (including NaN and Infinity)
        if ((left instanceof Character || left instanceof Number) && (right instanceof Character || right instanceof Number)) {
            Double leftDouble = getDoubleValue(left);
            Double rightDouble = getDoubleValue(right);
            if (leftDouble.equals(rightDouble)) {
                return null;
            }
            return new Difference("Different primitive values.", left, right, fieldStack);
        }
        // check if java objects are equal
        if (left.getClass().getName().startsWith("java.lang")) {
            if (left.equals(right)) {
                return null;
            }
            return new Difference("Different object values.", left, right, fieldStack);
        }
        // check if enums are equal
        if (left instanceof Enum && right instanceof Enum) {
            if (left.equals(right)) {
                return null;
            }
            return new Difference("Different enum values.", left, right, fieldStack);
        }
        return chainedComparator.doGetDifference(left, right, fieldStack, traversedInstancePairs);
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
