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
import org.unitils.reflectionassert.comparator.Comparator;
import org.unitils.reflectionassert.difference.Difference;

/**
 * A comparator that compares primitive types by value and not by type.
 * You can for example compare a double with an integer value.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientNumberComparator implements Comparator {


    /**
     * Returns true if both objects are not null and instances of Number or Character.
     *
     * @param left  The left object
     * @param right The right object
     * @return True for Numbers and Charaters
     */
    public boolean canCompare(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        if ((left instanceof Character || left instanceof Number) && (right instanceof Character || right instanceof Number)) {
            return true;
        }
        return false;
    }

    /**
     * Compares the two values by converting them to a double and comparing these double values.
     *
     * @param left                 The left Number or Character, not null
     * @param right                The right Number or Character, not null
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return A difference if the values are different, null otherwise
     */
    public Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        // check if right and left have same number value (including NaN and Infinity)
        Double leftDouble = getDoubleValue(left);
        Double rightDouble = getDoubleValue(right);
        if (!leftDouble.equals(rightDouble)) {
            return new Difference("Different primitive values", left, right);
        }
        return null;
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
