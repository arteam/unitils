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
 * Comparator for simple cases.
 * Following cases are handled: left and right are the same instance, left or right have a null value,
 * left or right are enumerations, left or right are java.lang classes and
 * left or right are of type Character or Number
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class SimpleCasesComparator implements Comparator {


    /**
     * Returns true if both object are the same instance, have a null value, are an Enum/Number/Character or
     * are a java.lang type.
     *
     * @param left  The left object
     * @param right The right object
     * @return True for simple cases
     */
    public boolean canCompare(Object left, Object right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return true;
        }
        if ((left instanceof Character || left instanceof Number) && (right instanceof Character || right instanceof Number)) {
            return true;
        }
        if (left.getClass().getName().startsWith("java.lang") || right.getClass().getName().startsWith("java.lang")) {
            return true;
        }
        if (left instanceof Enum && right instanceof Enum) {
            return true;
        }
        return false;
    }


    /**
     * Compares the given values.
     *
     * @param left                 The left value
     * @param right                The right value
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return A Difference if both values are different, null otherwise
     */
    public Difference compare(Object left, Object right, ReflectionComparator reflectionComparator) {
        // check if the same instance is referenced
        if (left == right) {
            return null;
        }
        // check if the left value is null
        if (left == null) {
            return new Difference("Left value null", left, right);
        }
        // check if the right value is null
        if (right == null) {
            return new Difference("Right value null", left, right);
        }
        // check if right and left have same number value (including NaN and Infinity)
        if ((left instanceof Character || left instanceof Number) && (right instanceof Character || right instanceof Number)) {
            Double leftDouble = getDoubleValue(left);
            Double rightDouble = getDoubleValue(right);
            if (leftDouble.equals(rightDouble)) {
                return null;
            }
            return new Difference("Different primitive values", left, right);
        }
        // check if java objects are equal
        if (left.getClass().getName().startsWith("java.lang") || right.getClass().getName().startsWith("java.lang")) {
            if (left.equals(right)) {
                return null;
            }
            return new Difference("Different object values", left, right);
        }
        // check if enums are equal
        if (left instanceof Enum && right instanceof Enum) {
            if (left.equals(right)) {
                return null;
            }
            return new Difference("Different enum values", left, right);
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
