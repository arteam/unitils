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

import java.util.Date;

/**
 * Comparator that checks whether 2 dates are both null or not null, the actual time-value is not compared.
 * This can be useful when the actual time/date is not known is advance but you still want to check whether
 * a value has been set or not. E.g. a last modification timestamp in the databsse.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class LenientDatesComparator implements Comparator {


    /**
     * Returns true if both objects are null or both objects are Date instances.
     *
     * @param left  The left object
     * @param right The right object
     * @return True if null or dates
     */
    public boolean canCompare(Object left, Object right) {
        if (right == null && left == null) {
            return true;
        }
        if (right instanceof Date && left instanceof Date) {
            return true;
        }
        if ((right == null && left instanceof Date) || (left == null && right instanceof Date)) {
            return true;
        }
        return false;
    }


    /**
     * Compares the given dates.
     *
     * @param left                 The left date
     * @param right                The right date
     * @param onlyFirstDifference  True if only the first difference should be returned
     * @param reflectionComparator The root comparator for inner comparisons, not null
     * @return A difference if one of the dates is null and the other one not, else null
     */
    public Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        if ((right == null && left instanceof Date) || (left == null && right instanceof Date)) {
            return new Difference("Lenient dates, but not both instantiated or both null", left, right);
        }
        return null;
    }
}
